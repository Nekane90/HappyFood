package com.example.happyfood.controllers;


import happyDAO.FavoritoDao;
import javafx.application.Platform;
import happyDTO.UsuarioDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;

import happyDTO.RecetaDto;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MisFavoritosController extends MenuLateralController implements Initializable,AvatarActualizable{

    @FXML
    private GridPane gpMenu;
    @FXML
    private Button btVolver;
    @FXML private Circle circuloAvatar;
    @FXML private MenuButton menuLateral;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarFavoritos();
        configurarMenuComun(menuLateral, this);
        actualizarAvatarUsuario();
    }


    private void cargarFavoritos() {
        System.out.println("DEBUG: Iniciando carga de favoritos...");

        int idUsuario = Sesion.getUsuario().getId();
        System.out.println("DEBUG: Buscando favoritos para el usuario ID: " + idUsuario);

        FavoritoDao favoritoDao = new FavoritoDao();
        List<RecetaDto> misRecetas = favoritoDao.obtenerRecetasFavoritas(idUsuario);

        if (misRecetas == null || misRecetas.isEmpty()) {
            System.out.println("DEBUG: La lista de la base de datos está VACÍA o es NULL.");
        } else {
            System.out.println("DEBUG: Se han encontrado " + misRecetas.size() + " recetas.");

            gpMenu.getChildren().clear();
            int columnas = 4;
            int fila = 0;
            int col = 0;

            for (RecetaDto receta : misRecetas) {
                System.out.println("DEBUG: Pintando receta: " + receta.getTitulo());
                VBox tarjeta = crearCuadradoReceta(receta);
                gpMenu.add(tarjeta, col, fila);

                col++;
                if (col == columnas) {
                    col = 0;
                    fila++;
                }
            }
        }
        gpMenu.requestLayout(); // Fuerza al Grid a recalcular el espacio
        System.out.println("DEBUG: Layout solicitado para gpMenu");
    }
    public void actualizarAvatarUsuario() {
        UsuarioDto usuario = Sesion.getUsuario();
        if (usuario != null && usuario.getAvatar() != null) {
            cargarImagenEnCirculo(usuario.getAvatar());
        } else {
            cargarImagenEnCirculo("animal_1.png");
        }
    }
    @FXML



    private void cargarImagenEnCirculo(String nombreImagen) {
        try {
            String ruta = "/imagenes/avatares/" + nombreImagen;
            var recurso = getClass().getResource(ruta);
            if (recurso != null) {
                Image img = new Image(recurso.toExternalForm());
                circuloAvatar.setFill(new ImagePattern(img));
            }
        } catch (Exception e) {
            System.err.println("Error en avatar: " + e.getMessage());
        }
    }

    private void abrirDetalleReceta(RecetaDto receta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/detalle_receta.fxml"));
            Parent root = loader.load();

            DetalleRecetaController controller = loader.getController();

            controller.setReceta(receta);

            Stage stage = new Stage();
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/com/example/happyfood/estilos.css").toExternalForm());

            stage.setTitle("Preparación: ");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setMaximized(true);
            // Hacerla modal
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            boolean estadoFinal = controller.isEsFavorito();

        } catch (IOException e) {
            System.err.println("Error al abrir el detalle de la receta");
            e.printStackTrace();
        }
    }

    private VBox crearCuadradoReceta(RecetaDto receta) {
        VBox card = new VBox();
        card.getStyleClass().add("tarjeta-receta");
        card.setAlignment(Pos.CENTER);
        card.setSpacing(8);
        card.setPadding(new Insets(15));
        card.setPrefSize(250, 280);
        card.setMinWidth(200);
        card.setMinHeight(250);
        card.setVisible(true);

        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        ImageView img = new ImageView();
        String url = receta.getUrlImagen();
        try {
            if (url != null && !url.trim().isEmpty()) {
                img.setImage(new Image(url.trim(), true));
            } else {
                img.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
            }
        } catch (Exception e) {
            img.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
        }
        img.setFitWidth(180);
        img.setFitHeight(130);
        img.setPreserveRatio(true);


        String tituloOriginal = receta.getTitulo();
        Label lbTitulo = new Label(tituloOriginal);
        lbTitulo.setWrapText(true);
        lbTitulo.setTextAlignment(TextAlignment.CENTER);
        lbTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1b4332;");

        new Thread(() -> {
            try {

                String traducido = TraductorService.traducirFrase(tituloOriginal);

                Platform.runLater(() -> lbTitulo.setText(traducido));
            } catch (Exception ex) {
                System.err.println("❌ No se pudo traducir: " + tituloOriginal);
            }
        }).start();

        card.getChildren().addAll(img, lbTitulo);

        card.setOnMouseClicked(e -> {
            abrirDetalleReceta(receta);
        });

        card.setCursor(Cursor.HAND);

        card.setOnMouseEntered(e -> {
            card.setScaleX(1.03);
            card.setScaleY(1.03);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 8);");
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        });

        return card;
    }

    @FXML
    public void volverPantallaPrincipal(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/principal.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            scene.setRoot(root);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/com/example/happyfood/estilos.css").toExternalForm());

            stage.setTitle("Happy Food - Menú Principal");
            stage.setMaximized(true);

        } catch (IOException e) {
            System.err.println("Error al cargar el FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al aplicar estilos: " + e.getMessage());
        }
    }


}
