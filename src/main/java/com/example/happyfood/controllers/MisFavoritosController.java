package com.example.happyfood.controllers;


import happyDAO.FavoritoDao;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
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
        System.out.println("DEBUG: Iniciando carga de favoritos..."); // <-- Control 1

        int idUsuario = Sesion.getUsuario().getId();
        System.out.println("DEBUG: Buscando favoritos para el usuario ID: " + idUsuario); // <-- Control 2

        FavoritoDao favoritoDao = new FavoritoDao();
        List<RecetaDto> misRecetas = favoritoDao.obtenerRecetasFavoritas(idUsuario);

        if (misRecetas == null || misRecetas.isEmpty()) {
            System.out.println("DEBUG: La lista de la base de datos está VACÍA o es NULL."); // <-- Control 3
        } else {
            System.out.println("DEBUG: Se han encontrado " + misRecetas.size() + " recetas."); // <-- Control 4

            gpMenu.getChildren().clear();
            int columnas = 4;
            int fila = 0;
            int col = 0;

            for (RecetaDto receta : misRecetas) {
                System.out.println("DEBUG: Pintando receta: " + receta.getTitulo()); // <-- Control 5
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

    private VBox crearTarjeta(RecetaDto receta) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(15));

        // Estilo de cuadrado blanco con bordes redondeados y sombra
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        card.setPrefSize(220, 220); // Tamaño del cuadrado

        // Imagen
        ImageView img = new ImageView(new Image(receta.getUrlImagen()));
        img.setFitHeight(120);
        img.setFitWidth(180);
        img.setPreserveRatio(true);

        // Título
        Label name = new Label(receta.getTitulo());
        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1b4332;");
        name.setWrapText(true);
        name.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(img, name);

        card.setOnMouseEntered(e -> card.setScaleX(1.05));
        card.setOnMouseEntered(e -> card.setScaleY(1.05));
        card.setOnMouseExited(e -> card.setScaleX(1.0));
        card.setOnMouseExited(e -> card.setScaleY(1.0));

        return card;
    }

    private void abrirDetalleReceta(RecetaDto receta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/detalle_receta.fxml"));
            Parent root = loader.load();

            DetalleRecetaController controller = loader.getController();

            controller.setReceta(receta);


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalle de: " + receta.getTitulo());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

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
            System.err.println("⚠️ URL de imagen inválida para: " + receta.getTitulo());
            img.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
        }
        img.setFitWidth(180);
        img.setFitHeight(130);
        img.setPreserveRatio(true);

        Label lbTitulo = new Label(receta.getTitulo());
        lbTitulo.setWrapText(true);
        lbTitulo.setTextAlignment(TextAlignment.CENTER);
        lbTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1b4332;");

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
