package com.example.happyfood.controllers;

import com.example.happyfood.conexion.ConexionDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class ModificarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    @FXML private ComboBox<String> comboDieta;
    @FXML private CheckComboBox<String> comboIntolerancias;
    @FXML private Circle circuloVistaPrevia;
    @FXML
    private Button btVolver;

    private String nombreAvatarSeleccionado;
    @FXML
    public void abrirGaleriaAvatares() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/avatar.fxml"));
            Parent root = loader.load();

            AvatarControllers controllerAvatares = loader.getController();

            // Ahora esto ya no saldrá en rojo porque el método existe en AvatarControllers
            controllerAvatares.setControladorModificar(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Mapas de traducción (los mismos que tenías)
    private static final Map<String, String> MAPA_DIETAS = Map.of(
            "Sin Dieta", "none", "Vegana", "vegan", "Vegetariana", "vegetarian", "Sin Gluten", "gluten-free", "Mediterránea", "mediterranean"
    );
    private static final Map<String, String> MAPA_INTOLERANCIAS = Map.of(
            "Lactosa", "lactose",
            "Gluten", "gluten",
            "Frutos Secos", "nuts",
            "Marisco", "shellfish",
            "Huevo", "egg"
    );
    @FXML
    public void initialize() {

        comboDieta.getItems().addAll("Sin Dieta", "Vegana", "Vegetariana", "Sin Gluten", "Mediterránea");
        comboIntolerancias.getItems().addAll("Lactosa", "Gluten", "Frutos Secos", "Marisco", "Huevo");


        UsuarioDto usuario = Sesion.getUsuario();
        if (usuario != null) {
            txtNombre.setText(usuario.getNombreUsuario());
            txtEmail.setText(usuario.getEmail());
            txtPassword.setText(usuario.getPassword());

            // Cargar Avatar
            this.nombreAvatarSeleccionado = usuario.getAvatar();
            cargarImagenAvatar(this.nombreAvatarSeleccionado);

            // Seleccionar Dieta actual
            MAPA_DIETAS.forEach((esp, ing) -> {
                if (ing.equals(usuario.getTipoDieta())) comboDieta.setValue(esp);
            });


            if (usuario.getIntolerancias() != null) {
                String[] ints = usuario.getIntolerancias().split(", ");
                for (String s : ints) {

                    comboIntolerancias.getCheckModel().check(traducirAEspanol(s));
                }
            }
        }
    }


    @FXML
    public void guardarCambios(ActionEvent event) {
        UsuarioDto usuarioActual = Sesion.getUsuario();

        // 1. Traducir dieta
        String dietaBD = MAPA_DIETAS.getOrDefault(comboDieta.getValue(), "none");

        // 2. Procesar intolerancias (QUITAMOS EL ESPACIO EN EL JOINING)
        String intoleranciasBD = comboIntolerancias.getCheckModel().getCheckedItems()
                .stream()
                .map(item -> MAPA_INTOLERANCIAS.getOrDefault(item, item.toLowerCase()))
                .collect(Collectors.joining(",")); // Formato: "lactose,nuts"

        // 3. Lectura segura de textos
        String nombre = (txtNombre.getText() == null) ? "" : txtNombre.getText().trim();
        String email = (txtEmail.getText() == null) ? "" : txtEmail.getText().trim();
        String password = (txtPassword.getText() == null) ? "" : txtPassword.getText().trim();

        if (nombre.isEmpty()) nombre = usuarioActual.getNombreUsuario();
        if (email.isEmpty()) email = usuarioActual.getEmail();
        if (password.isEmpty()) password = usuarioActual.getPassword();

        Connection con = ConexionDB.conectar();
        if (con != null) {
            String sql = "UPDATE usuarios SET nombre_usuario=?, email=?, password=?, intolerancias=?, tipo_dieta=?, imagen=? WHERE id=?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, intoleranciasBD);
                stmt.setString(5, dietaBD);
                stmt.setString(6, this.nombreAvatarSeleccionado);
                stmt.setInt(7, usuarioActual.getId());

                if (stmt.executeUpdate() > 0) {
                    // ACTUALIZAMOS EL OBJETO LOCAL
                    usuarioActual.setNombreUsuario(nombre);
                    usuarioActual.setEmail(email);
                    usuarioActual.setPassword(password); // No olvides actualizar la pass en el objeto
                    usuarioActual.setAvatar(this.nombreAvatarSeleccionado);
                    usuarioActual.setTipoDieta(dietaBD);
                    usuarioActual.setIntolerancias(intoleranciasBD);

                    // ACTUALIZAMOS LA SESIÓN GLOBAL (IMPORTANTE)
                    Sesion.setUsuario(usuarioActual);

                    mostrarAlerta("Éxito", "Tus datos han sido actualizados.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarImagenAvatar(String nombreImagen) {
        try {
            String ruta = "/com/example/happyfood/imagenes/avatares/" + nombreImagen;
            Image img = new Image(getClass().getResource(ruta).toExternalForm());
            circuloVistaPrevia.setFill(new ImagePattern(img));
        } catch (Exception e) {
            System.err.println("No se pudo cargar el avatar actual.");
        }
    }

    // Método auxiliar para el CheckComboBox al revés
    private String traducirAEspanol(String ingles) {
        return MAPA_INTOLERANCIAS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(ingles))
                .map(Map.Entry::getKey)
                .findFirst().orElse(ingles);
    }

    // Método auxiliar para mostrar mensajes al usuario
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void volverPantallaPrincipal(ActionEvent event){
        Stage stage = (Stage) btVolver.getScene().getWindow();
        stage.close();

    }

}
