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

                // --- TRUCO PARA EVITAR RECORTES ---
                // 1. Limpiamos HTML
                // 2. Reemplazamos saltos de línea por puntos o espacios temporales
                // para que Google no se detenga en la primera línea.
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
                // ... tu catch actual ...
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

        // Obtener el ID real de Spoonacular

        int idApi = recetaJson.get("id").getAsInt();

        String titulo = recetaJson.get("title").getAsString();

        // Aseguramos que la URL sea la que guardamos en la pantalla anterior
        String urlImg = recetaJson.get("image").getAsString();

        Thread t = new Thread(() -> {
            try {
                FavoritoDao favDao = new FavoritoDao();
                RecetaDao recDao = new RecetaDao();

                if (esFavorito) {
                    // Usamos el idApi real para guardar en nuestra BD
                    RecetaDto receta = new RecetaDto(titulo, urlImg, idApi);
                    int idLocal = recDao.asegurarRecetaEnBD(receta);
                    favDao.guardarFavorito(idUsuario, idLocal);
                    System.out.println("❤️ Favorito guardado con ID API: " + idApi);
                } else {
                    // Buscamos por el ID real para eliminar
                    int idLocal = recDao.obtenerIdPorApi(idApi);
                    if (idLocal != -1) {
                        favDao.eliminarFavorito(idUsuario, idLocal);
                        System.out.println("💔 Favorito eliminado con ID API: " + idApi);
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




}
