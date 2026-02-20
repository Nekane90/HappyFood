package com.example.happyfood;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AltaUsuarioController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
