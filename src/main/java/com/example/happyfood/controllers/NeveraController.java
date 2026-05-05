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

    private final String API_KEY = "0508d38ac42c4b7e9011e615ee80611a";
    private final String BASE_IMAGE_URL = "https://spoonacular.com/cdn/ingredients_100x100/";

    // Aquí guardamos los nombres de lo que el usuario ha seleccionado
    private List<String> miDespensa = new ArrayList<>();
    private PrincipalController mainController;

    public void setMainController(PrincipalController main) {
        this.mainController = main;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Nevera inicializando...");

        // Usamos un hilo para esperar un momento antes de cargar los ingredientes iniciales
        Thread delay = new Thread(() -> {
            try {
                Thread.sleep(1000); // Espera 1 segundo
                System.out.println("Cargando ingredientes iniciales ahora...");
                cargarIngredienteDesdeAPI("milk,egg,cheese,tomato,apple");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        delay.setDaemon(true);
        delay.start();
    }

    @FXML
    void añadirIngrediente() {
        String query = txtBuscador.getText().trim();
        if (!query.isEmpty()) {
            cargarIngredienteDesdeAPI(query);
            txtBuscador.clear();
        }
    }

    private void cargarIngredienteDesdeAPI(String query) {
        Thread thread = new Thread(() -> {
            try {
                String urlS = "https://api.spoonacular.com/food/ingredients/search?query="
                        + query.toLowerCase().replace(" ", "%20")
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
                            String nombre = ing.get("name").getAsString();
                            String imagen = BASE_IMAGE_URL + ing.get("image").getAsString();

                            dibujarIngrediente(nombre, imagen);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void dibujarIngrediente(String nombre, String urlImagen) {
        // Creamos la tarjeta visual
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        String baseStyle = "-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-cursor: hand;";
        card.setStyle(baseStyle);

        // Intentamos cargar la imagen
        ImageView iv = new ImageView();
        try {
            Image img = new Image(urlImagen, true); // Carga asíncrona
            iv.setImage(img);
            iv.setFitWidth(55);
            iv.setFitHeight(55);
            iv.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Error con la imagen de " + nombre);
        }

        Label lbl = new Label(nombre.toUpperCase());
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

        card.getChildren().addAll(iv, lbl);

        // Evento de selección (Borde verde)
        card.setOnMouseClicked(e -> {
            if (miDespensa.contains(nombre)) {
                miDespensa.remove(nombre);
                card.setStyle(baseStyle);
            } else {
                miDespensa.add(nombre);
                card.setStyle(baseStyle + "-fx-border-color: #2d6a4f; -fx-border-width: 3; -fx-border-radius: 12;");
            }
            System.out.println("Cesta: " + miDespensa);
        });

        // ¡CRÍTICO!: Añadir al panel en el hilo de la interfaz
        Platform.runLater(() -> {
            panelIngredientes.getChildren().add(card);
            System.out.println("Dibujado en pantalla: " + nombre);
        });
    }

    @FXML
    void buscarRecetas() {
        if (miDespensa.isEmpty()) {
            System.out.println("No has seleccionado ingredientes.");
            return;
        }

        String ingredientesParaAPI = String.join(",", miDespensa);

        Thread thread = new Thread(() -> {
            try {
                // Nueva URL para buscar RECETAS (no ingredientes)
                String url = "https://api.spoonacular.com/recipes/findByIngredients?ingredients="
                        + ingredientesParaAPI + "&number=5&ranking=1&apiKey=" + API_KEY;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonArray recetas = JsonParser.parseString(response.body()).getAsJsonArray();

                    Platform.runLater(() -> {
                        // Borramos los ingredientes para mostrar los platos (o podrías abrir una ventana nueva)
                        panelIngredientes.getChildren().clear();

                        for (int i = 0; i < recetas.size(); i++) {
                            JsonObject receta = recetas.get(i).getAsJsonObject();
                            String titulo = receta.get("title").getAsString();
                            String imgPlato = receta.get("image").getAsString();

                            mostrarTarjetaReceta(titulo, imgPlato);
                        }
                    });
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // Método para dibujar los platos encontrados
    private void mostrarTarjetaReceta(String titulo, String urlImg) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        card.setPrefWidth(250);

        ImageView iv = new ImageView(new Image(urlImg, true));
        iv.setFitWidth(200); iv.setFitHeight(150); iv.setPreserveRatio(true);

        Label lbl = new Label(titulo.toUpperCase());
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1b4332;");

        card.getChildren().addAll(iv, lbl);
        panelIngredientes.getChildren().add(card);
    }



    @FXML
    void limpiarNevera() {
        panelIngredientes.getChildren().clear();
        miDespensa.clear();
    }

}