package com.example.happyfood;

import com.example.happyfood.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Cargamos el FXML desde la carpeta de recursos
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/happyfood/login.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 650, 450);
        stage.setTitle("Bienvenid@");
        stage.setScene(scene);
        /// centro la pantalla
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(); // Lanza la aplicación (esto llamará automáticamente a start)
    }
}
