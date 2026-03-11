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
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import javafx.event.ActionEvent;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class AltaUsuarioController {


    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    @FXML private ComboBox<String> comboDieta;
    @FXML private CheckComboBox<String> comboIntolerancias; // De la librería ControlsFX
    @FXML
    private Circle circuloVistaPrevia;

    // Variable para guardar el nombre y enviarlo luego a la Base de Datos
    private String nombreAvatarSeleccionado = "animal_1.png";

    @FXML
    public void abrirGaleriaAvatares() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/avatar.fxml"));
            Parent root = loader.load();

            AvatarControllers controllerAvatares = loader.getController();

            // Ahora esto ya no saldrá en rojo porque el método existe en AvatarControllers
            controllerAvatares.setControladorAlta(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Opcional: para que no pinchen atrás hasta elegir
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cambiarFotoAvatar(Image nuevaImagen, String nombreArchivo) {
        circuloVistaPrevia.setFill(new ImagePattern(nuevaImagen));
        this.nombreAvatarSeleccionado = nombreArchivo;
    }
    @FXML
    public void initialize() {
        // Llenamos el combo de dietas al abrir la pantalla
        comboDieta.getItems().addAll( "Sin Dieta","Vegana", "Vegetariana", "Sin Gluten", "Mediterránea");

        // Llenamos las opciones de intolerancias
        comboIntolerancias.getItems().addAll("Lactosa", "Gluten", "Frutos Secos", "Marisco", "Huevo");
    }
    private static final Map<String, String> MAPA_DIETAS = Map.of(
            "Sin Dieta", "none",
            "Vegana", "vegan",
            "Vegetariana", "vegetarian",
            "Sin Gluten", "gluten-free",
            "Mediterránea", "mediterranean"
    );

    private static final Map<String, String> MAPA_INTOLERANCIAS = Map.of(
            "Lactosa", "lactose",
            "Gluten", "gluten",
            "Frutos Secos", "nuts",
            "Marisco", "shellfish",
            "Huevo", "egg"
    );
    @FXML

    public void insertar(ActionEvent event) {
        // 1. Obtener y Traducir Dieta
        String dietaSeleccionada = (comboDieta.getValue() != null) ? comboDieta.getValue().toString() : "Sin Dieta";
        String dietaBD = MAPA_DIETAS.getOrDefault(dietaSeleccionada, "none");

        // 2. Obtener y Traducir Intolerancias (CheckComboBox)
        // Convertimos la lista de Strings en español a una sola cadena en inglés separada por comas
        String intoleranciasBD = comboIntolerancias.getCheckModel().getCheckedItems()
                .stream()
                .map(item -> MAPA_INTOLERANCIAS.getOrDefault(item, item.toLowerCase()))
                .collect(Collectors.joining(", "));

        // 3. Resto de datos
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        // --- Validaciones (Igual que antes) ---
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Campos obligatorios vacíos.");
            return;
        }

        // 4. Inserción en BD
        Connection con = ConexionDB.conectar();
        if (con != null) {
            String sql = "INSERT INTO usuarios (nombre_usuario, email, password, intolerancias, tipo_dieta, imagen) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, intoleranciasBD);
                stmt.setString(5, dietaBD);
                stmt.setString(6, this.nombreAvatarSeleccionado);

                if (stmt.executeUpdate() > 0) {
                    mostrarAlerta("Éxito", "Usuario guardado.");
                    limpiarCampos();
                }
            } catch (SQLException e) {

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
