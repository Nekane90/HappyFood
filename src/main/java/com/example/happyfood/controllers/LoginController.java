package com.example.happyfood.controllers;

import com.example.happyfood.conexion.ConexionDB;
import happyDTO.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoginController  {
    @FXML
    private TextField tfNombreUsuario;

    @FXML
    private PasswordField pfPassword;


    /// metodo que llama a la pantalla principal
    @FXML
    private void manejarBotonEntrar(ActionEvent event) {
            comprobarUsuario(event);


    }

    private void comprobarUsuario(ActionEvent event) {
        Connection con = ConexionDB.conectar();
        if (con == null) return;

        String nombreUsuario = tfNombreUsuario.getText();
        String password = pfPassword.getText();

        try {
            String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND password = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, nombreUsuario);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // --- LOGIN CORRECTO: GUARDAMOS SESIÓN ---
                int id = rs.getInt("id");
                String dieta = rs.getString("tipo_dieta");
                String intoleranciasStr = rs.getString("intolerancias");

                List<String> listaAlergias = new ArrayList<>();
                if (intoleranciasStr != null && !intoleranciasStr.isEmpty()) {
                    // Usamos una ArrayList
                    listaAlergias = new ArrayList<>(Arrays.asList(intoleranciasStr.split(",")));
                }

                Sesion.setUsuario(new Usuario(id, listaAlergias, dieta));

                // CAMBIO DE PANTALLA ---
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/principal.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1200, 700);

                    scene.getStylesheets().add(getClass().getResource("/com/example/happyfood/estilos.css").toExternalForm());
                    stage.setTitle("Mi Menú");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.setMaximized(true);
                    stage.show();

                    System.out.println("✅ Acceso concedido.");

                } catch (IOException e) {
                    System.err.println("Error al cargar principal.fxml: " + e.getMessage());
                }

            } else {
                // --- LOGIN INCORRECTO ---
                System.out.println("❌ Datos incorrectos.");
                mostrarAlerta("Error de Login", "Usuario o contraseña incorrectos");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    public void llamadapantallaAlta(ActionEvent event) {
        try {
            // 1. Cargar el archivo FXML de la pantalla de alta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/alltaUsuario.fxml"));
            Parent root = loader.load();

            // 2. Obtener la ventana actual (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 530);


            stage.setScene(scene);
            stage.setTitle("Registro de Usuario - HappyFood");
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar la pantalla de registro.");
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
