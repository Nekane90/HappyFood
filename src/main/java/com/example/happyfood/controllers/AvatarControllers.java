package com.example.happyfood.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


public class AvatarControllers {

    private AltaUsuarioController altaController;
    private ModificarUsuarioController modificar; // Asegúrate de que el nombre sea 'modificar' o 'controladorModificar' según lo uses

    public void setControladorAlta(AltaUsuarioController altaController) {
        this.altaController = altaController;
    }

    public void setControladorModificar(ModificarUsuarioController modificar) {
        this.modificar = modificar;
    }

    @FXML
    public void seleccionarAvatar(MouseEvent event) {
        Circle pulsado = (Circle) event.getSource();
        ImagePattern pattern = (ImagePattern) pulsado.getFill();
        Image imgElegida = pattern.getImage();

        // Sacamos el nombre del archivo de la URL
        String url = imgElegida.getUrl();
        String nombreArchivo = url.substring(url.lastIndexOf("/") + 1);

        // LÓGICA PARA ALTA (Ya te funciona)
        if (altaController != null) {
            altaController.cambiarFotoAvatar(imgElegida, nombreArchivo);
        }

        // LÓGICA PARA MODIFICAR (Añade esto para que use el nuevo método)
        if (modificar != null) {
            modificar.cambiarFotoAvatar(imgElegida, nombreArchivo);
        }

        // Cerramos la ventana de avatares
        Stage stage = (Stage) pulsado.getScene().getWindow();
        stage.close();
    }
}
