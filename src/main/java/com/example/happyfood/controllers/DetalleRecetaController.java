package com.example.happyfood.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import happyDAO.FavoritoDao;
import happyDAO.RecetaDao;
import happyDTO.RecetaDto;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class DetalleRecetaController {

    @FXML
    private Label lbTitulo;
    @FXML
    private ImageView imgReceta;
    @FXML
    private TextArea taReceta;
    @FXML
    private Button btVolver;
    @FXML
    private Button btFavorito;

    private boolean esFavorito;
    private JsonObject recetaJson;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (lbTitulo.getScene() != null && lbTitulo.getScene().getWindow() != null) {
                Stage stage = (Stage) lbTitulo.getScene().getWindow();
                stage.setOnCloseRequest(event -> Lector.detenerVoz());
            }
        });
    }


    /// metodo que carga la receta con su imagen
    public void initData(String titulo, String urlImg, JsonObject recetaJson,boolean favoritoInicial) {

        this.recetaJson = recetaJson;
        this.esFavorito = favoritoInicial;
        actualizarIconoFavorito();

        lbTitulo.setText(titulo);
        taReceta.setWrapText(true);
        taReceta.setEditable(false);
        taReceta.setText("Traduciendo receta...");

        // Dentro de initData en DetalleRecetaController.java
        if (urlImg != null && !urlImg.trim().isEmpty()) {
            try {
                String urlLimpia = urlImg.trim().replace("http://", "https://");
                Image img = new Image(urlLimpia, true); // true = carga en background
                img.errorProperty().addListener((obs, oldV, isError) -> {
                    if (isError) {
                        Platform.runLater(() -> {
                            imgReceta.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
                        });
                    }
                });
                imgReceta.setImage(img);

            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ URL inválida en Detalle: " + urlImg);
                imgReceta.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
            }
        } else {
            imgReceta.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
        }
        // TRADUCCIÓN EN SEGUNDO PLANO
        Thread threadTraduccion = new Thread(() -> {
            try {
                int idApi = recetaJson.get("id").getAsInt();
                ApiController apiController = new ApiController();

                String jsonDetalleString = apiController.obtenerDetallesReceta(idApi);
                JsonObject jsonCompleto = JsonParser.parseString(jsonDetalleString).getAsJsonObject();

                if (jsonCompleto != null) {
                    this.recetaJson = jsonCompleto;
                    System.out.println("✅ JSON actualizado con detalles completos.");
                }

                // 2. A partir de aquí, usa 'this.recetaJson' para todo
                String tituloEs = TraductorService.traducirFrase(titulo);

                String instruccionesEn = "";
                if (this.recetaJson.has("instructions") && !this.recetaJson.get("instructions").isJsonNull()) {
                    instruccionesEn = this.recetaJson.get("instructions").getAsString();
                } else {
                    instruccionesEn = this.recetaJson.get("summary").getAsString();
                }

                String textoLimpio = instruccionesEn.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
                String instruccionesEs = TraductorService.traducirFrase(textoLimpio);

                Platform.runLater(() -> {
                    lbTitulo.setText(tituloEs);
                    taReceta.setText(instruccionesEs.replace(". ", ".\n\n"));
                });

            } catch (Exception e) {
                System.err.println("Error cargando detalles: " + e.getMessage());
            }
        });

        threadTraduccion.setDaemon(true);
        threadTraduccion.start();
    }
    @FXML
    private void btnEscucharReceta() {
        String titulo = lbTitulo.getText();
        String contenido = taReceta.getText();

        if (contenido == null || contenido.trim().isEmpty() || contenido.equals("Traduciendo receta...")) {
            System.out.println("⚠️ No hay texto para leer todavía.");
            return;
        }

        // Limpiamos etiquetas y saltos de línea raros
        String textoParaLeer = (titulo + ". " + contenido).replaceAll("<[^>]*>", "");

        System.out.println("DEBUG: Intentando leer texto de longitud: " + textoParaLeer.length());

        Lector.leerEnVozAlta(textoParaLeer);
    }
    @FXML
    private void manejarFavorito(ActionEvent event) {
        esFavorito = !esFavorito;
        actualizarIconoFavorito();

        int idUsuario = Sesion.getUsuario().getId();
        int idApi = recetaJson.get("id").getAsInt();
        String titulo = recetaJson.get("title").getAsString();
        String urlImg = recetaJson.get("image").getAsString();

        int tiempoAux = 0;
        if (recetaJson.has("readyInMinutes")) {
            tiempoAux = recetaJson.get("readyInMinutes").getAsInt();
        }

        String dificultadAux = "Media";
        if (recetaJson.has("spoonacularScore")) {
            double score = recetaJson.get("spoonacularScore").getAsDouble();
            if (score > 80) dificultadAux = "Fácil";
            else if (score < 40) dificultadAux = "Difícil";
        }
        String instrucciones = "";
        if (recetaJson.has("instructions") && !recetaJson.get("instructions").isJsonNull()) {
            instrucciones = recetaJson.get("instructions").getAsString();
        }
        final int tiempoFinal = tiempoAux;
        final String dificultadFinal = dificultadAux;
        final String fInstrucciones = instrucciones;
        // ----------------------------------------------------------

        Thread t = new Thread(() -> {
            try {
                FavoritoDao favDao = new FavoritoDao();
                RecetaDao recDao = new RecetaDao();

                if (esFavorito) {
                    RecetaDto receta = new RecetaDto(titulo, urlImg, idApi);
                    receta.setTiempoPreparacion(tiempoFinal);
                    receta.setDificultad(dificultadFinal);
                    receta.setInstrucciones(fInstrucciones);

                    int idLocal = recDao.asegurarRecetaEnBD(receta);
                    favDao.guardarFavorito(idUsuario, idLocal);
                    System.out.println("❤️ Favorito guardado con tiempo: " + tiempoFinal + " y dificultad: " + dificultadFinal);
                } else {
                    int idLocal = recDao.obtenerIdPorApi(idApi);
                    if (idLocal != -1) {
                        favDao.eliminarFavorito(idUsuario, idLocal);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void actualizarIconoFavorito() {
        Platform.runLater(() -> {
            try {
                String ruta = esFavorito ? "/imagenes/corazon-relleno-rojo.png" : "/imagenes/corazon-contorno-rojo.png";
                Image img = new Image(getClass().getResourceAsStream(ruta));

                // Si el botón no tiene nada dentro, creamos el ImageView
                if (btFavorito.getGraphic() == null) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(25);
                    iv.setFitHeight(25);
                    iv.setPreserveRatio(true);
                    btFavorito.setGraphic(iv);
                } else {
                    // Si ya tiene algo, intentamos convertirlo a ImageView y cambiar la imagen
                    Node grafico = btFavorito.getGraphic();
                    if (grafico instanceof ImageView) {
                        ((ImageView) grafico).setImage(img);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al actualizar icono: " + e.getMessage());
            }
        });
    }



    //Metodo que vuelve atras del boton
    public void volverPantallaPrincipal(ActionEvent event){
        Lector.detenerVoz();
        Stage stage = (Stage) btVolver.getScene().getWindow();
        stage.close();

    }

    //getter
    public boolean isEsFavorito() { return esFavorito; }

    public void setReceta(RecetaDto receta) {

        com.google.gson.JsonObject jsonSimulado = new com.google.gson.JsonObject();
        jsonSimulado.addProperty("id", receta.getIdApi());
        jsonSimulado.addProperty("title", receta.getTitulo());
        jsonSimulado.addProperty("image", receta.getUrlImagen());
        if(receta.getInstrucciones() != null) {
            jsonSimulado.addProperty("instructions", receta.getInstrucciones());
        }

        // 2. Llamamos a tu método original que ya hace todo el trabajo de imagen y traducción
        initData(receta.getTitulo(), receta.getUrlImagen(), jsonSimulado, true);
    }



}
