package com.example.happyfood.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;


public abstract class MenuLateralController {


    protected void configurarMenuComun(MenuButton menuLateral, AvatarActualizable controllerRef) {
        menuLateral.getItems().forEach(item -> {
            if (item.getId() != null) {
                switch (item.getId()) {
                    case "btnSalir" -> item.setOnAction(e -> salir(menuLateral));
                    case "btnCuenta" -> {
                        item.setOnAction(e -> abrirCuenta(menuLateral, controllerRef));
                    }
                }
            }
        });
    }

    public void abrirCuenta(MenuButton ancla, AvatarActualizable controllerRef) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/modificarUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Configuración de Usuario");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();

            // Al cerrar, llamamos al método de la interfaz
            if (controllerRef != null) {
                controllerRef.actualizarAvatarUsuario();
            }

        } catch (IOException e) {
            System.err.println("ERROR: No se pudo cargar el FXML.");
            e.printStackTrace();
        }
    }
    protected void salir (MenuButton referencia){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/happyfood/login.fxml"));
            Stage stageActual = (Stage) referencia.getScene().getWindow();
            stageActual.close();

            Stage stageLogin = new Stage();
            stageLogin.setTitle("Login - HappyFood");
            stageLogin.setScene(new Scene(root));
            stageLogin.show();
        } catch (IOException e) {
            System.err.println("Error al volver al login: " + e.getMessage());
        }
    }
}

