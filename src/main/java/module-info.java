module com.example.happyfood {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;


    opens com.example.happyfood to javafx.fxml;
    exports com.example.happyfood;

    exports com.example.happyfood.controllers;
    opens com.example.happyfood.controllers to javafx.fxml;
}