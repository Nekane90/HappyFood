package com.example.happyfood;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckComboBox;


public class AltaUsuarioController {
    @FXML
    private Label welcomeText;
    private CheckComboBox<String> comboIntolerancias;
    @FXML
    public void initialize() {
        // Así le metes los datos por código
        comboIntolerancias.getItems().addAll("Gluten", "Lactosa", "Frutos Secos", "Huevo");
    }
}
