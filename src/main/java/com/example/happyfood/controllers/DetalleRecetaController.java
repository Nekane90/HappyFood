package com.example.happyfood.controllers;

import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.awt.*;



public class DetalleRecetaController {

    @FXML
    private Label lbTitulo;
    @FXML
    private ImageView imgReceta;
    @FXML
    private TextArea taReceta;


    /// metodo que carga la receta con su imagen
    public void initData(String titulo, String urlImg, JsonObject recetaJson) {
        lbTitulo.setText(titulo);

        if (urlImg != null && !urlImg.isEmpty()) {
            try {

                String urlLimpia = urlImg.trim().replace("http://", "https://");

                Image img = new Image(urlLimpia, true);

                imgReceta.setImage(img);
            } catch (Exception e) {
                System.err.println("Error cargando imagen: " + e.getMessage());
            }
        }
    }



}
