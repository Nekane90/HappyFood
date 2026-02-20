package com.example.happyfood.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
<<<<<<< HEAD
import org.controlsfx.control.CheckComboBox;

=======
>>>>>>> ramaPrincipal

public class AltaUsuarioController {
    @FXML
    private Label welcomeText;
<<<<<<< HEAD
    private CheckComboBox<String> comboIntolerancias;
    @FXML
    public void initialize() {
        // Así le metes los datos por código
        comboIntolerancias.getItems().addAll("Gluten", "Lactosa", "Frutos Secos", "Huevo");
=======

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
>>>>>>> ramaPrincipal
    }
}
