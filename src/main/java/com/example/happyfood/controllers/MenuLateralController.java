package com.example.happyfood.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class MenuLateralController {

    // Este método lo heredarán todos

    protected void configurarMenuComun(MenuButton menuLateral, PrincipalController mainRef) {
        // Buscamos los items por su ID o los creamos por código
        menuLateral.getItems().forEach(item -> {
            switch (item.getId()) {
                case "btnMisMenus" -> item.setOnAction(e -> abrirHistorial(mainRef));
                //case "btnFavoritos" -> item.setOnAction(e -> abrirFavoritos());
                //case "btnCuenta" -> item.setOnAction(e -> abrirCuenta());
                //case "btnSalir" -> item.setOnAction(e -> System.exit(0));
            }
        });
    }

    protected void abrirHistorial(PrincipalController main) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/historialMenu.fxml"));
            Parent root = loader.load();
            HistorialMenuController controller = loader.getController();
            controller.setMainController(main); // Pasamos la referencia para que pueda cargar el JSON

            Stage stage = new Stage();
            stage.setTitle("Mis Menús Guardados");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // aqui irian los demas metodos del menu desplegable)
}
