package com.example.happyfood.controllers;

import com.example.happyfood.conexion.ConexionDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import happyDTO.UsuarioDto;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class ModificarUsuarioController {

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

    private String nombreAvatarSeleccionado;

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
        // Cargar opciones en los combos
        comboDieta.getItems().addAll("Sin Dieta", "Vegana", "Vegetariana", "Sin Gluten", "Mediterránea");
        comboIntolerancias.getItems().addAll("Lactosa", "Gluten", "Frutos Secos", "Marisco", "Huevo");

        UsuarioDto usuario = Sesion.getUsuario();
        if (usuario != null) {
            txtNombre.setText(usuario.getNombreUsuario());
            txtEmail.setText(usuario.getEmail());

            // Carga de contraseña
            txtPassword.setText(usuario.getPassword());
            txtPasswordVisible.setText(usuario.getPassword());

            // Cargar Avatar
            this.nombreAvatarSeleccionado = usuario.getAvatar();
            cargarImagenPorNombre(this.nombreAvatarSeleccionado != null ? this.nombreAvatarSeleccionado : "animal_1.png");

            // Seleccionar Dieta actual
            MAPA_DIETAS.forEach((esp, ing) -> {
                if (ing.equals(usuario.getTipoDieta())) comboDieta.setValue(esp);
            });

            // Seleccionar Intolerancias actuales
            if (usuario.getIntolerancias() != null) {
                String[] ints = usuario.getIntolerancias().split(",");
                for (String s : ints) {
                    comboIntolerancias.getCheckModel().check(traducirAEspanol(s.trim()));
                }
            }
            evaluarFuerza();
        }
    }

    @FXML
    public void guardarCambios(ActionEvent event) {
        UsuarioDto usuarioActual = Sesion.getUsuario();

        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        // Obtener la contraseña del campo que esté activo en ese momento
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
            String sql = "UPDATE usuarios SET nombre_usuario=?, email=?, password=?, intolerancias=?, tipo_dieta=?, imagen=? WHERE id=?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setString(2, email);
                stmt.setString(3, passwordFinal);
                stmt.setString(4, intoleranciasBD);
                stmt.setString(5, dietaBD);
                stmt.setString(6, this.nombreAvatarSeleccionado);
                stmt.setInt(7, usuarioActual.getId());

                if (stmt.executeUpdate() > 0) {
                    usuarioActual.setNombreUsuario(nombre);
                    usuarioActual.setEmail(email);
                    usuarioActual.setPassword(passwordFinal);
                    usuarioActual.setAvatar(this.nombreAvatarSeleccionado);
                    usuarioActual.setTipoDieta(dietaBD);
                    usuarioActual.setIntolerancias(intoleranciasBD);
                    Sesion.setUsuario(usuarioActual);

                    mostrarAlerta("Éxito", "Tus datos han sido actualizados correctamente.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Error al guardar en la base de datos.");
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

        if (pass.length() < 2) {
            lblFuerza.setText("Seguridad: MUY BAJA");
            lblFuerza.setStyle("-fx-text-fill: #ff4d4d;");
            progressFuerza.setStyle("-fx-accent: #ff4d4d;");
        } else if (pass.length() < 6) {
            lblFuerza.setText("Seguridad: MEDIA");
            lblFuerza.setStyle("-fx-text-fill: #ffdb4d;");
            progressFuerza.setStyle("-fx-accent: #ffdb4d;");
        } else if (pass.length() < 8) {
            lblFuerza.setText("Seguridad: BUENA");
            lblFuerza.setStyle("-fx-text-fill: #4db8ff;");
            progressFuerza.setStyle("-fx-accent: #4db8ff;");
        } else {
            lblFuerza.setText("Seguridad: ALTA");
            lblFuerza.setStyle("-fx-text-fill: #2eb82e;");
            progressFuerza.setStyle("-fx-accent: #2eb82e;");
        }
    }

    private double calcularPuntaje(String pass) {
        double p = 0;
        if (pass.length() >= 2) p += 0.2;
        if (pass.length() >= 6) p += 0.3;
        if (pass.length() >= 8) p += 0.3;
        if (pass.matches(".*[A-Z].*") && pass.matches(".*[0-9].*")) p += 0.1;
        if (pass.matches(".*[!@#$%^&*()].*")) p += 0.1;
        return Math.min(p, 1.0);
    }

    private boolean esEmailValido(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }

    private void cargarImagenPorNombre(String nombreImagen) {
        try {
            String ruta = "/imagenes/avatares/" + (nombreImagen != null ? nombreImagen : "animal_1.png");
            var recurso = getClass().getResource(ruta);
            if (recurso != null) {
                circuloVistaPrevia.setFill(new ImagePattern(new Image(recurso.toExternalForm())));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: " + e.getMessage());
        }
    }

    @FXML
    public void abrirGaleriaAvatares() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/avatar.fxml"));
            Parent root = loader.load();
            AvatarControllers controllerAvatares = loader.getController();
            controllerAvatares.setControladorModificar(this);
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

    private String traducirAEspanol(String ingles) {
        return MAPA_INTOLERANCIAS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(ingles))
                .map(Map.Entry::getKey)
                .findFirst().orElse(ingles);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void volverPantallaPrincipal(ActionEvent event) {
        Stage stage = (Stage) btVolver.getScene().getWindow();
        stage.close();
    }
}