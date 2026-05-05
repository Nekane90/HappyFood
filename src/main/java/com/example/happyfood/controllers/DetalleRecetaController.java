package com.example.happyfood.controllers;

import com.google.gson.JsonObject;
import happyDAO.FavoritoDao;
import happyDAO.RecetaDao;
import happyDTO.RecetaDto;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;





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
                String tituloEs = TraductorService.traducirFrase(titulo);

                String instruccionesEn = "";
                if (recetaJson.has("instructions") && !recetaJson.get("instructions").isJsonNull()) {
                    instruccionesEn = recetaJson.get("instructions").getAsString();
                } else if (recetaJson.has("summary") && !recetaJson.get("summary").isJsonNull()) {
                    instruccionesEn = recetaJson.get("summary").getAsString();
                }

                String textoLimpio = instruccionesEn.replaceAll("<[^>]*>", " ") // Quitar HTML
                        .replaceAll("\\s+", " ")     // Quitar múltiples espacios/tabs
                        .trim();

                String instruccionesEs = "Instrucciones no disponibles.";
                if (!textoLimpio.isEmpty()) {
                    // Si el texto es muy largo, Google puede fallar.
                    // Para recetas normales, esto funcionará perfecto.
                    instruccionesEs = TraductorService.traducirFrase(textoLimpio);
                    System.out.println("Texto enviado a Google: " + textoLimpio);
                }

                String finalTitulo = tituloEs;
                String finalInstrucciones = instruccionesEs;

                Platform.runLater(() -> {
                    lbTitulo.setText(finalTitulo);
                    // Si quieres que el texto no sea una sola línea gigante,
                    // podemos añadir un formateo básico después de traducir
                    taReceta.setText(finalInstrucciones.replace(". ", ".\n\n"));
                });

            } catch (Exception e) {
            }
        });

        threadTraduccion.setDaemon(true);
        threadTraduccion.start();
    }




    @FXML
    private void manejarFavorito(ActionEvent event) {
        esFavorito = !esFavorito;
        actualizarIconoFavorito();

        int idUsuario = Sesion.getUsuario().getId();
        int idApi = recetaJson.get("id").getAsInt();
        String titulo = recetaJson.get("title").getAsString();
        String urlImg = recetaJson.get("image").getAsString();

        // --- NUEVO: Extraer tiempo y dificultad del JSON de la API ---
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
        final int tiempoFinal = tiempoAux;
        final String dificultadFinal = dificultadAux;
        // ----------------------------------------------------------

        Thread t = new Thread(() -> {
            try {
                FavoritoDao favDao = new FavoritoDao();
                RecetaDao recDao = new RecetaDao();

                if (esFavorito) {
                    RecetaDto receta = new RecetaDto(titulo, urlImg, idApi);
                    receta.setTiempoPreparacion(tiempoFinal);
                    receta.setDificultad(dificultadFinal);

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
        String ruta = esFavorito ? "/imagenes/corazon-relleno-rojo.png" : "/imagenes/corazon-contorno-rojo.png";
        Image img = new Image(getClass().getResourceAsStream(ruta));
        ImageView icono = (ImageView) btFavorito.getGraphic();
        icono.setImage(img);
    }

    //Metodo que vuelve atras del boton

    public void volverPantallaPrincipal(ActionEvent event){
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
