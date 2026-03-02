package com.example.happyfood.controllers;

import com.example.happyfood.conexion.ConexionDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;


public class LoginController  {
    @FXML
    private TextField tfNombreUsuario;

    @FXML
    private PasswordField pfPassword;


    /// metodo que llama a la pantalla principal
    @FXML
    private void manejarBotonEntrar(ActionEvent event) {
        try {
            comprobarUsuario();
            // 1. Cargo el nuevo FXML del Menú Principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/principal.fxml"));
            Parent root = loader.load();

            // 2.  el "Stage"  es (la ventana)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Creo la nueva escena con el FXML cargado y la pongo en la ventana
            //aqui le pongo las medidas que quiero que tenga la pantalla
            Scene scene = new Scene(root,1200, 700);

            //aqui carga el estilos.css
            scene.getStylesheets().add(getClass().getResource("/com/example/happyfood/estilos.css").toExternalForm());
            stage.setTitle("Mi Menú");
            stage.setScene(scene);
            /// aqui estoy centrando la pantalla
            stage.centerOnScreen();
            stage.show();
            // esto es por si se quiere poner que la pantalla ocupe toda la pantalla del pc
            stage.setMaximized(true);
            //esto es por si quieres que se pueda maximizar y minizar con el raton estirando la pantalla
            //stage.setResizable(true);

        } catch (IOException e) {
            //System.err.println("Error: No se pudo cargar el menú principal. Revisa la ruta.");
            System.err.println("Error detalle: " + e.getMessage());
        }

    }

    private void comprobarUsuario() {
        Connection con = ConexionDB.conectar();

        if (con == null) {
            System.err.println("🛑 No se puede comprobar usuario porque la conexión falló.");
            return;
        }
        String nombreUsuario = tfNombreUsuario.getText();
        String password = pfPassword.getText();

        try {
            String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND password = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, nombreUsuario);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Login correcto. Bienvenido: " + rs.getString("nombre_usuario"));
                // Aquí navegas a la siguiente pantalla
            } else {
                System.out.println("❌ Nombre de usuario o contraseña incorrectos.");
                // Aquí muestras un mensaje de error al usuario
            }

        } catch (SQLException e) {
            e.printStackTrace();
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

}
