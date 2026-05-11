package com.example.happyfood.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NeveraController implements Initializable {

    @FXML
    private TextField txtBuscador;
    @FXML
    private FlowPane panelIngredientes;
    @FXML
    private Button btVolver;
    private final String API_KEY = "0508d38ac42c4b7e9011e615ee80611a";
    private final String BASE_IMAGE_URL = "https://spoonacular.com/cdn/ingredients_100x100/";

    private List<String> miDespensa = new ArrayList<>();
    private PrincipalController mainController;

    public void setMainController(PrincipalController main) {
        this.mainController = main;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Carga inicial de cortesía
        cargarIngredienteDesdeAPI("milk,egg,cheese,tomato,apple");
    }

    @FXML
    void añadirIngrediente() {
        String queryEs = txtBuscador.getText().trim();
        if (!queryEs.isEmpty()) {
            // Traducimos de Español a Inglés para que la API lo encuentre
            Thread thread = new Thread(() -> {
                String queryEn = TraductorService.traducirAIngles(queryEs);
                Platform.runLater(() -> {
                    cargarIngredienteDesdeAPI(queryEn);
                    txtBuscador.clear();
                });
            });
            thread.start();
        }
    }

    private void cargarIngredienteDesdeAPI(String queryEn) {
        Thread thread = new Thread(() -> {
            try {
                String urlS = "https://api.spoonacular.com/food/ingredients/search?query="
                        + queryEn.toLowerCase().replace(" ", "%20")
                        + "&number=5&apiKey=" + API_KEY;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlS)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                    JsonArray results = jsonResponse.getAsJsonArray("results");

                    Platform.runLater(() -> {
                        for (int i = 0; i < results.size(); i++) {
                            JsonObject ing = results.get(i).getAsJsonObject();
                            String nombreEn = ing.get("name").getAsString();
                            String imagen = BASE_IMAGE_URL + ing.get("image").getAsString();
                            dibujarIngrediente(nombreEn, imagen);
                        }
                    });
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void dibujarIngrediente(String nombreEn, String urlImagen) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        String baseStyle = "-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-cursor: hand;";
        card.setStyle(baseStyle);

        ImageView iv = new ImageView(new Image(urlImagen, true));
        iv.setFitWidth(55); iv.setFitHeight(55); iv.setPreserveRatio(true);

        // Usamos el método traducirFrase (EN -> ES) de tu compañera
        Label lbl = new Label("...");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

        Thread t = new Thread(() -> {
            String nombreEs = TraductorService.traducirFrase(nombreEn);
            Platform.runLater(() -> lbl.setText(nombreEs.toUpperCase()));
        });
        t.start();

        card.getChildren().addAll(iv, lbl);

        card.setOnMouseClicked(e -> {
            if (miDespensa.contains(nombreEn)) {
                miDespensa.remove(nombreEn);
                card.setStyle(baseStyle);
            } else {
                miDespensa.add(nombreEn);
                card.setStyle(baseStyle + "-fx-border-color: #2d6a4f; -fx-border-width: 3; -fx-border-radius: 12;");
            }
        });

        Platform.runLater(() -> panelIngredientes.getChildren().add(card));
    }

    @FXML
    void buscarRecetas() {
        if (miDespensa.isEmpty()) return;
        String ingredientes = String.join(",", miDespensa);

        Thread thread = new Thread(() -> {
            try {
                String url = "https://api.spoonacular.com/recipes/findByIngredients?ingredients="
                        + ingredientes + "&number=5&apiKey=" + API_KEY;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonArray recetas = JsonParser.parseString(response.body()).getAsJsonArray();
                    Platform.runLater(() -> {
                        panelIngredientes.getChildren().clear();
                        for (int i = 0; i < recetas.size(); i++) {
                            JsonObject r = recetas.get(i).getAsJsonObject(); // El objeto completo

                            // Pasamos el objeto 'r' entero al método
                            mostrarTarjetaReceta(r);
                        }
                    });
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        thread.start();
    }

    private void mostrarTarjetaReceta(JsonObject recetaJson) {
        String tituloEn = recetaJson.get("title").getAsString();
        String urlImg = recetaJson.get("image").getAsString();

        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 20; -fx-cursor: hand;");
        card.setPrefWidth(220);

        ImageView iv = new ImageView(new Image(urlImg, true));
        iv.setFitWidth(180); iv.setFitHeight(120); iv.setPreserveRatio(true);

        Label lbl = new Label("Traduciendo...");
        lbl.setWrapText(true);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1b4332;");

        // Traducción del título (EN -> ES) para la tarjeta
        Thread t = new Thread(() -> {
            String tituloEs = TraductorService.traducirFrase(tituloEn);
            Platform.runLater(() -> lbl.setText(tituloEs.toUpperCase()));
        });
        t.start();

        card.getChildren().addAll(iv, lbl);

        // --- CLAVE: Al pinchar, abrimos el detalle de tu compañera ---
        card.setOnMouseClicked(e -> abrirDetalleReceta(recetaJson, lbl.getText()));

        panelIngredientes.getChildren().add(card);
    }

    @FXML
    void limpiarNevera() {
        panelIngredientes.getChildren().clear();
        miDespensa.clear();
    }

    private void obtenerPreparacion(int id, String tituloEs) {
        Thread thread = new Thread(() -> {
            try {
                // URL para obtener información detallada de la receta
                String url = "https://api.spoonacular.com/recipes/" + id + "/information?apiKey=" + API_KEY;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject detalle = JsonParser.parseString(response.body()).getAsJsonObject();

                    // Sacamos las instrucciones (pueden venir en HTML o texto)
                    String instruccionesEn = detalle.get("instructions").isJsonNull() ?
                            "No instructions available." :
                            detalle.get("instructions").getAsString().replaceAll("<[^>]*>", "");

                    // Usamos el traductor de tu compañera (EN -> ES)
                    String instruccionesEs = TraductorService.traducirFrase(instruccionesEn);

                    Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        alert.setTitle("Preparación: " + tituloEs);
                        alert.setHeaderText(tituloEs);
                        alert.setContentText(instruccionesEs);
                        alert.getDialogPane().setPrefSize(500, 400); // Para que se vea bien el texto largo
                        alert.showAndWait();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
    private void abrirDetalleReceta(JsonObject recetaJson, String tituloEs) {
        try {
            // 1. Cargamos el FXML (ajusta el nombre exacto de la vista si es distinto)
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/happyfood/detalle_receta.fxml"));
            javafx.scene.Parent root = loader.load();

            // 2. Obtenemos el controlador
            DetalleRecetaController controller = loader.getController();

            // 3. Pasamos los datos iniciales
            String urlImg = recetaJson.get("image").getAsString();

            // Llamamos al método de tu compañera que dispara la carga y traducción de pasos
            controller.initData(tituloEs, urlImg, recetaJson, false);

            // 4. Creamos y mostramos la nueva ventana (Stage)
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("HappyFood - Detalle de Receta");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error al abrir DetalleReceta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    public void volverPrincipal(javafx.event.ActionEvent actionEvent) {
        Stage stage = (Stage) btVolver.getScene().getWindow();
        stage.close();
    }
}