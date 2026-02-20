module com.example.happyfood {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.happyfood to javafx.fxml;
    exports com.example.happyfood;

    opens com.example.happyfood.controllers to javafx.fxml;
    exports com.example.happyfood.controllers;

    /*opens com.example.happyfood.conexion to javafx.fxml;
    exports com.example.happyfood.conexion;
    */
      /*opens com.example.happyfood.happyDao to javafx.fxml;
    exports com.example.happyfood.happyDao;
    */
      /*opens com.example.happyfood.happyDto to javafx.fxml;
    exports com.example.happyfood.happyDto;
    */


}