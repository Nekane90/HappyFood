package com.example.happyfood.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

<<<<<<< HEAD
public class LoginController extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/happyfood/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 450);
        stage.setTitle("Bienvenid@");
        stage.setScene(scene);
        stage.show();
=======
public class LoginController  {


    /// metodo que llama a la pantalla principal
    @FXML
    private void manejarBotonEntrar(ActionEvent event) {
        try {
            // 1. Cargo el nuevo FXML del Menú Principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/principal.fxml"));
            Parent root = loader.load();

            // 2.  el "Stage"  es (la ventana)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Creo la nueva escena con el FXML cargado y la pongo en la ventana
            //aqui le pongo las medidas que quiero que tenga la pantalla
            Scene scene = new Scene(root,1200, 700);
            stage.setTitle("Mi Menú");
            stage.setScene(scene);
            /// aqui estoy centrando la pantalla
            stage.centerOnScreen();
            stage.show();
            // esto es por si se quiere poner que la pantalla ocupe toda la pantalla del pc
            //stage.setMaximized(true);
            //esto es por si quieres que se pueda maximizar y minizar con el raton estirando la pantalla
            //stage.setResizable(true);

        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar el menú principal. Revisa la ruta.");
        }
>>>>>>> ramaPrincipal
    }



}
