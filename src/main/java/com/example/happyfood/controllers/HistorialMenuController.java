package com.example.happyfood.controllers;

import happyDAO.PlanificadorSemanalDao;
import happyDTO.PlanificadorSemanalDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
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
        listMenus.getItems().setAll(dao.obtenerPlanesPorUsuario(idUser));

        // Configurar el doble clic para cargar el plan
        listMenus.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                cargarPlanSeleccionado();
            }
        });
    }

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
}


