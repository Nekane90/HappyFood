package com.example.happyfood.controllers;

import happyDAO.PlanificadorSemanalDao;
import happyDTO.PlanificadorSemanalDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HistorialMenuController implements Initializable {
    @FXML
    private ListView<PlanificadorSemanalDto> listMenus;
    private PrincipalController mainController;

    public void setMainController(PrincipalController main) {
        this.mainController = main;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar los planes del usuario desde el DAO
        PlanificadorSemanalDao dao = new PlanificadorSemanalDao();
        int idUser = Sesion.getUsuario().getId();
        List<PlanificadorSemanalDto> lista = dao.obtenerPlanesPorUsuario(idUser);
        if (lista != null) {
            listMenus.getItems().setAll(lista);
        }
        //  CONFIGURAR EL DISEÑO DE LA CELDA
        listMenus.setCellFactory(lv -> new ListCell<PlanificadorSemanalDto>() {
            @Override
            protected void updateItem(PlanificadorSemanalDto item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Creamos un contenedor vertical para el texto
                    VBox contenedor = new VBox(2); // 2px de espacio entre líneas
                    contenedor.setPadding(new Insets(5, 10, 5, 10));

                    // Título: Nombre del plan (Grande y Negrita)
                    Label lblNombre = new Label(item.getNombre_menu());
                    lblNombre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                    // Subtítulo: Fecha (Más pequeño y gris)
                    Label lblFecha = new Label("📅 Guardado el: " + item.getFecha());
                    lblFecha.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                    contenedor.getChildren().addAll(lblNombre, lblFecha);

                    // Le decimos a la celda que dibuje nuestro contenedor en vez de texto plano
                    setGraphic(contenedor);
                }
            }
        });

        // Configurar el doble clic para cargar el plan
        listMenus.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                cargarPlanSeleccionado();
            }
        });
    }
    //Carga el menu seleccionado en la principal
    @FXML
    private void cargarPlanSeleccionado() {
        PlanificadorSemanalDto seleccionado = listMenus.getSelectionModel().getSelectedItem();

        if (seleccionado != null && mainController != null) {
            // Pasamos el JSON y el Set de favoritos
            mainController.procesarMenuCompleto(seleccionado.getJson(), mainController.getMisFavoritos());

            // Cerramos la ventana de historial
            Stage stage = (Stage) listMenus.getScene().getWindow();
            stage.close();
        }
    }

    //Para el boton de eliminar menu
    @FXML
    private void eliminarMenu() {
        //  Obtener el plan seleccionado
        PlanificadorSemanalDto seleccionado = listMenus.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un menú de la lista para eliminarlo.", Alert.AlertType.WARNING);
            return;
        }

        //  Pedir confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Menú");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que quieres borrar el menú: " + seleccionado.getNombre_menu() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            //  Llamar al DAO
            PlanificadorSemanalDao dao = new PlanificadorSemanalDao();
            boolean borradoExitoso = dao.eliminarPlan(seleccionado.getId());

            if (borradoExitoso) {
                // --- ESTA ES LA PARTE CLAVE PARA LA ACTUALIZACIÓN VISUAL ---
                listMenus.getItems().remove(seleccionado);
                listMenus.getSelectionModel().clearSelection(); // Limpiamos la selección

                // Mensaje de éxito
                mostrarAlerta("Éxito", "Menú borrado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se ha podido eliminar el menú de la base de datos.", Alert.AlertType.ERROR);
            }
        }
    }

    // Método auxiliar para no repetir código de alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void handleExportarPDF() {
        PlanificadorSemanalDto seleccionado = listMenus.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarAlerta("Procesando", "Estamos generando tu PDF. Se abrirá automáticamente al terminar. Lo encontrarás en descargas");

            new Thread(() -> {
                try {
                    String jsonBBDD = seleccionado.getJson();
                    String nombreParaElArchivo = seleccionado.getNombre_menu();

                    // servicio de PDF
                    PdfService pdfService = new PdfService();
                    pdfService.generarPdfDesdeJson(nombreParaElArchivo, jsonBBDD);

                    System.out.println("✅ PDF terminado en segundo plano.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            mostrarAlerta("Atención", "Por favor, selecciona un menú de la lista.");
        }
    }

    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}


