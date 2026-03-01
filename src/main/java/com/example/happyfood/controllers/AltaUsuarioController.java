package com.example.happyfood.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckComboBox;

public class AltaUsuarioController {

    @FXML
    private Label welcomeText;

    @FXML
    private CheckComboBox<String> comboIntolerancias;

    @FXML
    public void initialize() {
        // Así le metes los datos por código
        if (comboIntolerancias != null) {
            comboIntolerancias.getItems().addAll("Gluten", "Lactosa", "Frutos Secos", "Huevo");
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
