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
        System.out.println("Intentando cargar imagen en: " + imgReceta);
        System.out.println("Detalle cargado. ¿Viene como favorito?: " + favoritoInicial);


        if (urlImg != null && !urlImg.isEmpty()) {
            String urlLimpia = urlImg.trim().replace("http://", "https://");
            Image img = new Image(urlLimpia, true); // Carga en background

            img.errorProperty().addListener((obs, oldV, isError) -> {
                if (isError) {
                    // Si salta el error de certificado (PKIX), ponemos el logo local
                    Platform.runLater(() -> {
                        imgReceta.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
                        System.out.println("⚠️ Error de certificado/red. Cargando logo local.");
                    });
                }
            });

            imgReceta.setImage(img);
        }

        /*if (urlImg != null && !urlImg.isEmpty()) {
            System.out.println("DEBUG: La URL recibida es: " + urlImg);
            try {

                String urlLimpia = urlImg.trim().replace("http://", "https://");

                Image img = new Image(urlLimpia, true);

                imgReceta.setImage(img);
            } catch (Exception e) {
                System.err.println("Error cargando imagen: " + e.getMessage());
            }
        }*/
        if (recetaJson.has("instructions")) {
            String instrucciones = recetaJson.get("instructions").getAsString();
            taReceta.setText(instrucciones);
            taReceta.setWrapText(true); // Ajuste de línea automático
            taReceta.setEditable(false); // No queremos que el usuario las borre
        } else {
            taReceta.setText("Instrucciones no disponibles para esta receta.");
        }
    }

    @FXML
    private void manejarFavorito(ActionEvent event) {
        esFavorito = !esFavorito;
        actualizarIconoFavorito();

        int idUsuario = Sesion.getUsuario().getId();
        String titulo = recetaJson.get("title").getAsString();
        String urlImg = recetaJson.get("image").getAsString();
        int idApi = generarIdFicticio(titulo);

        Thread t = new Thread(() -> {
            try {
                FavoritoDao favDao = new FavoritoDao();
                RecetaDao recDao = new RecetaDao();

                if (esFavorito) {
                    RecetaDto receta = new RecetaDto(titulo, urlImg, idApi);
                    int idLocal = recDao.asegurarRecetaEnBD(receta);
                    favDao.guardarFavorito(idUsuario, idLocal);
                } else {
                    int idLocal = recDao.obtenerIdPorApi(idApi);
                    if (idLocal != -1) favDao.eliminarFavorito(idUsuario, idLocal);
                }

                // Actualizamos la UI en el hilo principal
                //Platform.runLater(this::actualizarIconoFavorito);

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

    /// para crear el "id de a api" mientras no puedo conectarme a ella
    private int generarIdFicticio(String titulo) {
        return Math.abs(titulo.toLowerCase().trim().hashCode());
    }



}
