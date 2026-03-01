package com.example.happyfood.controllers;

import com.example.happyfood.conexion.ConexionDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AltaUsuarioController {


    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    @FXML private ComboBox<String> comboDieta;
    @FXML private CheckComboBox<String> comboIntolerancias; // De la librería ControlsFX


    @FXML
    public void initialize() {
        // Llenamos el combo de dietas al abrir la pantalla
        comboDieta.getItems().addAll( "Vegana", "Vegetariana", "Sin Gluten", "Mediterránea");

        // Llenamos las opciones de intolerancias
        comboIntolerancias.getItems().addAll("Lactosa", "Gluten", "Frutos Secos", "Marisco", "Huevo");
    }

    @FXML
    public void insertar(ActionEvent event) {
        // 1. Obtener datos
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String dieta = (comboDieta.getValue() != null) ? comboDieta.getValue().toString() : "";
        // Para CheckComboBox de ControlsFX
        String intolerancias = String.join(", ", comboIntolerancias.getCheckModel().getCheckedItems());

        // 2. Comprobar campos vacíos
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || dieta.isEmpty()) {
            mostrarAlerta("Error de Validación", "Todos los campos son obligatorios.");
            return;
        }

        // 3. Comprobar formato de correo (Regex)
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            mostrarAlerta("Error de Correo", "Por favor, introduce un email válido (ejemplo@correo.com).");
            return;
        }

        // insertar
        Connection con = ConexionDB.conectar();
        if (con != null) {
            try {

                String sql = "INSERT INTO usuarios (nombre_usuario, email, password, intolerancias, tipo_dieta) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, nombre);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, intolerancias);
                stmt.setString(5, dieta);

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    mostrarAlerta("Éxito", "Usuario registrado correctamente.");
                    limpiarCampos(); // Borrar campos tras éxito
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("duplicate key")) {
                    mostrarAlerta("Error", "Este usuario o correo ya existe.");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    // Método para borrar los campos
    private void limpiarCampos() {
        txtNombre.clear();
        txtEmail.clear();
        txtPassword.clear();
        comboDieta.setValue(null);
        if (comboIntolerancias != null) {
            comboIntolerancias.getCheckModel().clearChecks();
        }
    }
    // Método auxiliar para mostrar mensajes al usuario
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void volverLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Usamos un tamaño más cómodo como hablamos antes
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Login - HappyFood");
            stage.show();
        } catch (IOException e) {
            System.err.println("No se pudo cargar la pantalla de Login.");
        }
    }

}
