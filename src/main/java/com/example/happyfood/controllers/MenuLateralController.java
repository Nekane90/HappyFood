package com.example.happyfood.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

// IMPORTANTE: Asegúrate de que este sea el import correcto (JavaFX, no AWT)
import javafx.event.ActionEvent;
import java.io.IOException;

public abstract class MenuLateralController {

    protected void configurarMenuComun(MenuButton menuLateral, PrincipalController mainRef) {
        menuLateral.getItems().forEach(item -> {
            if (item.getId() != null) {
                switch (item.getId()) {
                    // Ahora pasamos mainRef para poder actualizar el avatar al volver

                    case "btnSalir" -> item.setOnAction(e -> salir(menuLateral));

                    case "btnCuenta" -> {
                        System.out.println("DEBUG: Intentando abrir cuenta...");
                        item.setOnAction(e -> abrirCuenta(menuLateral, mainRef));
                    }
                }
            }
        });
    }

    protected void salir(MenuButton referencia) {
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

    // En MenuLateralController.java
    protected void abrirCuenta(MenuButton ancla, PrincipalController mainRef) {
        try {
            // REVISIÓN CRÍTICA: ¿El archivo se llama modificar_usuario.fxml o modificarUsuario.fxml?
            // Debe coincidir letra por letra con tu archivo en resources.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/modificarUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Configuración de Usuario");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // showAndWait es clave: detiene el código aquí hasta que se cierre la ventana
            stage.showAndWait();

            // Al cerrar, si tenemos la referencia, refrescamos el avatar
            if (mainRef != null) {
                mainRef.actualizarAvatarUsuario();
            }

        } catch (IOException e) {
            System.err.println("ERROR: No se pudo cargar el FXML de modificar usuario.");
            e.printStackTrace(); // Esto te dirá en la consola EXACTAMENTE qué falló
        }
    }
}