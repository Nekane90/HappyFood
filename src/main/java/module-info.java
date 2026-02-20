module com.example.happyfood {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;


    opens com.example.happyfood to javafx.fxml;
    exports com.example.happyfood;
}