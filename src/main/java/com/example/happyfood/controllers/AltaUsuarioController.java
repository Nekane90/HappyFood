package com.example.happyfood.controllers;

import com.example.happyfood.conexion.ConexionDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class AltaUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> comboDieta;
    @FXML private CheckComboBox<String> comboIntolerancias;
    @FXML private Circle circuloVistaPrevia;
    @FXML private Button btVolver;

    // Elementos de contraseña y seguridad
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private ToggleButton btnVerPassword;
    @FXML private ProgressBar progressFuerza;
    @FXML private Label lblFuerza;

    private String nombreAvatarSeleccionado = "animal_1.png"; // Default para nuevos

    private static final Map<String, String> MAPA_DIETAS = Map.of(
            "Sin Dieta", "none", "Vegana", "vegan", "Vegetariana", "vegetarian",
            "Sin Gluten", "gluten-free", "Mediterránea", "mediterranean"
    );
    private static final Map<String, String> MAPA_INTOLERANCIAS = Map.of(
            "Lactosa", "lactose", "Gluten", "gluten", "Frutos Secos", "nuts",
            "Marisco", "shellfish", "Huevo", "egg"
    );

    @FXML
    public void initialize() {
        comboDieta.getItems().addAll("Sin Dieta", "Vegana", "Vegetariana", "Sin Gluten", "Mediterránea");
        comboIntolerancias.getItems().addAll("Lactosa", "Gluten", "Frutos Secos", "Marisco", "Huevo");
        comboDieta.setValue("Sin Dieta");

        cargarImagenPorNombre(nombreAvatarSeleccionado);
    }

    @FXML
    public void registrarUsuario(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String passwordFinal = btnVerPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();

        if (nombre.isEmpty() || email.isEmpty() || passwordFinal.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, rellena todos los campos.");
            return;
        }

        if (!esEmailValido(email)) {
            mostrarAlerta("Email inválido", "El formato del correo electrónico no es correcto.");
            return;
        }

        String dietaBD = MAPA_DIETAS.getOrDefault(comboDieta.getValue(), "none");
        String intoleranciasBD = comboIntolerancias.getCheckModel().getCheckedItems()
                .stream()
                .map(item -> MAPA_INTOLERANCIAS.getOrDefault(item, item.toLowerCase()))
                .collect(Collectors.joining(","));

        Connection con = ConexionDB.conectar();
        if (con != null) {
            String sql = "INSERT INTO usuarios (nombre_usuario, email, password, intolerancias, tipo_dieta, imagen) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setString(2, email);
                stmt.setString(3, passwordFinal);
                stmt.setString(4, intoleranciasBD);
                stmt.setString(5, dietaBD);
                stmt.setString(6, this.nombreAvatarSeleccionado);

                if (stmt.executeUpdate() > 0) {
                    mostrarAlerta("Registro Exitoso", "Usuario creado correctamente. Ya puedes iniciar sesión.");
                    volverLogin(null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo registrar el usuario. El email podría estar duplicado.");
            }
        }
    }

    @FXML
    public void togglePassword() {
        if (btnVerPassword.isSelected()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPassword.setVisible(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPasswordVisible.setVisible(false);
        }
    }

    @FXML
    public void evaluarFuerza() {
        String pass = btnVerPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();
        if (pass == null || pass.isEmpty()) {
            progressFuerza.setVisible(false);
            lblFuerza.setVisible(false);
            return;
        }
        progressFuerza.setVisible(true);
        lblFuerza.setVisible(true);

        double puntaje = calcularPuntaje(pass);
        progressFuerza.setProgress(puntaje);

        if (pass.length() < 6) {
            lblFuerza.setText("Seguridad: BAJA");
            lblFuerza.setStyle("-fx-text-fill: #ff4d4d;");
            progressFuerza.setStyle("-fx-accent: #ff4d4d;");
        } else if (pass.length() < 10) {
            lblFuerza.setText("Seguridad: MEDIA");
            lblFuerza.setStyle("-fx-text-fill: #ffdb4d;");
            progressFuerza.setStyle("-fx-accent: #ffdb4d;");
        } else {
            lblFuerza.setText("Seguridad: ALTA");
            lblFuerza.setStyle("-fx-text-fill: #2eb82e;");
            progressFuerza.setStyle("-fx-accent: #2eb82e;");
        }
    }

    private double calcularPuntaje(String pass) {
        double p = 0;
        if (pass.length() >= 4) p += 0.3;
        if (pass.length() >= 8) p += 0.3;
        if (pass.matches(".*[A-Z].*") && pass.matches(".*[0-9].*")) p += 0.2;
        if (pass.matches(".*[!@#$%^&*()].*")) p += 0.2;
        return Math.min(p, 1.0);
    }

    private boolean esEmailValido(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    private void cargarImagenPorNombre(String nombreImagen) {
        try {
            String ruta = "/imagenes/avatares/" + nombreImagen;
            var recurso = getClass().getResource(ruta);
            if (recurso != null) {
                circuloVistaPrevia.setFill(new ImagePattern(new Image(recurso.toExternalForm())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirGaleriaAvatares() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/avatar.fxml"));
            Parent root = loader.load();
            AvatarControllers controller = loader.getController();
            // IMPORTANTE: Asegúrate de que en AvatarControllers tengas un método setControladorAlta
            controller.setControladorAlta(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cambiarFotoAvatar(Image nuevaImagen, String nombreArchivo) {
        circuloVistaPrevia.setFill(new ImagePattern(nuevaImagen));
        this.nombreAvatarSeleccionado = nombreArchivo;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


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