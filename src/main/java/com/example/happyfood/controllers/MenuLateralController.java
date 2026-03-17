package com.example.happyfood.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public abstract class MenuLateralController {

    // Este método lo heredarán todos

    protected void configurarMenuComun(MenuButton menuLateral, PrincipalController mainRef) {
        // Buscamos los items por su ID o los creamos por código
        menuLateral.getItems().forEach(item -> {
            switch (item.getId()) {
                //case "btnMisMenus" -> item.setOnAction(e -> abrirHistorial(mainRef));
                //case "btnFavoritos" -> item.setOnAction(e -> abrirFavoritos());
                case "btnCuenta" -> item.setOnAction(e -> abrirCuenta(menuLateral));
                case "btnSalir" -> item.setOnAction(e -> salir(menuLateral));
            }
        });
    }




    protected void salir(MenuButton referencia) {
        try {
            // 1. Cargamos el FXML del Login de forma simple
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/happyfood/login.fxml"));

            // 2. Cerramos la ventana actual usando el MenuButton como ancla
            Stage stageActual = (Stage) referencia.getScene().getWindow();
            stageActual.close();

            // 3. Abrimos el Login en una ventana nueva
            Stage stageLogin = new Stage();
            stageLogin.setTitle("Login - HappyFood");
            stageLogin.setScene(new Scene(root));
            stageLogin.show();

        } catch (IOException e) {
            System.err.println("Error al volver al login: " + e.getMessage());
        }
    }

    protected void abrirCuenta(MenuButton ancla) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/modificarUsuario.fxml"));
            Parent root = loader.load();


            Stage stage = new Stage();
            stage.setTitle("Configuración de Usuario");
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la de atrás
            stage.setScene(new Scene(root));


            stage.show();


        } catch (IOException e) {
            System.err.println("Error al cargar modificarusuario.fxml. Revisa la ruta.");
            e.printStackTrace();
        }
    }
}
