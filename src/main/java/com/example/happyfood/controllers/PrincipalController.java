package com.example.happyfood.controllers;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PrincipalController {

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/happyfood/principal.fmxl"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 450);
        stage.setTitle("Principal");
        stage.setScene(scene);
        stage.show();
    }
}

