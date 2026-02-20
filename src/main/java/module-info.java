module com.example.happyfood {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.happyfood to javafx.fxml;
    exports com.example.happyfood;

    exports com.example.happyfood.controllers;
    opens com.example.happyfood.controllers to javafx.fxml;
}