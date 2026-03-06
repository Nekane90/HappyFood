package com.example.happyfood.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


public class AvatarControllers {
    // 1. Añadimos la variable para guardar la referencia de la pantalla de Alta
    private AltaUsuarioController altaController;

    // 2. Este es el método que antes te salía en rojo
    public void setControladorAlta(AltaUsuarioController altaController) {
        this.altaController = altaController;
    }

    @FXML
    public void seleccionarAvatar(MouseEvent event) {
        Circle pulsado = (Circle) event.getSource();
        ImagePattern pattern = (ImagePattern) pulsado.getFill();
        Image imgElegida = pattern.getImage();

        // Sacamos el nombre del archivo de la URL
        String url = imgElegida.getUrl();
        String nombreArchivo = url.substring(url.lastIndexOf("/") + 1);

        if (altaController != null) {
            // Llamamos al método de la pantalla de Alta pasándole la imagen y el nombre
            altaController.cambiarFotoAvatar(imgElegida, nombreArchivo);
        }

        // Cerramos la ventana de avatares
        Stage stage = (Stage) pulsado.getScene().getWindow();
        stage.close();
    }
}
