package com.example.happyfood.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import happyDAO.FavoritoDao;
import happyDAO.RecetaDao;
import happyDTO.RecetaDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;


public class PrincipalController {
    @FXML
    private GridPane gpMenu;
    @FXML
    private Label lbTitulo;
    @FXML
    private VBox lunesDesayuno;
    @FXML
    private VBox lunesComida;
    @FXML
    private VBox lunesCena;
    @FXML
    private VBox martesDesayuno;
    @FXML
    private VBox martesComida;
    @FXML
    private VBox martesCena;
    @FXML
    private VBox miercolesDesayuno;
    @FXML
    private VBox miercolesComida;
    @FXML
    private VBox miercolesCena;
    @FXML
    private VBox juevesDesayuno;
    @FXML
    private VBox juevesComida;
    @FXML
    private VBox juevesCena;
    @FXML
    private VBox viernesDesayuno;
    @FXML
    private VBox viernesComida;
    @FXML
    private VBox viernesCena;
    @FXML
    private VBox sabadoDesayuno;
    @FXML
    private VBox sabadoComida;
    @FXML
    private VBox sabadoCena;
    @FXML
    private VBox domingoDesayuno;
    @FXML
    private VBox domingoComida;
    @FXML
    private VBox domingoCena;
    @FXML
    private ProgressIndicator spinnerCarga;
    @FXML
    Button btCargarMenu;


    @FXML
    public void initialize() {
        configurarTitulos();
        // botonCorazon();
    }

    public void configurarTitulos() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        String[] comidas = {"Desayuno", "Almuerzo", "Cena"};

        // 1. Poner los Días en la fila 0 (empezando en la columna 1)
        for (int i = 0; i < dias.length; i++) {
            Label labelDia = new Label(dias[i]);
            labelDia.getStyleClass().add("titulo-grid"); // Aplicamos CSS
            gpMenu.add(labelDia, i + 1, 0);
            GridPane.setHalignment(labelDia, HPos.CENTER);
        }

        // 2. Poner las Comidas en la columna 0 (empezando en la fila 1)
        for (int j = 0; j < comidas.length; j++) {
            Label labelComida = new Label(comidas[j]);
            labelComida.getStyleClass().add("titulo-categoria");
            gpMenu.add(labelComida, 0, j + 1);
            GridPane.setHalignment(labelComida, HPos.CENTER);
        }
    }

    /// /ESTE ES UN CODIGO PARA PROBAR COMO SE VE EN LA VENTANA, EN LA OFI NO ME DEJA CONECTAR CON LA API
    @FXML
    private void manejarBotonBuscar(ActionEvent event) {
        // 1. Este es el texto que ya tienes funcionando
        //
        // String resultadoJson = "{ 'results': [ { 'title': 'Pasta con tomate', 'image': 'https://example.com/pasta.jpg' } ] }";
        String resultadoJson = "{ 'results': [ { 'title': 'Pasta con tomate', 'image': 'https://spoonacular.com/recipeImages/654857-312x231.jpg' } ] }";

        try {
            // 2. Convertimos el texto en un "Objeto JSON"
            JsonObject objetoCompleto = JsonParser.parseString(resultadoJson).getAsJsonObject();

            // 3. Sacamos la lista que se llama "results"
            JsonArray listaDeRecetas = objetoCompleto.getAsJsonArray("results");

            // 4. Recorremos la lista (aunque solo tenga una)
            for (int i = 0; i < listaDeRecetas.size(); i++) {
                JsonObject receta = listaDeRecetas.get(i).getAsJsonObject();

                // 5. ¡Aquí tienes los datos directamente!
                String titulo = receta.get("title").getAsString();
                String urlImagen = receta.get("image").getAsString();
                // LLAMAMOS A LA FUNCIÓN PARA LAS CELDAS QUE QUERAMOS
                // ponerRecetaEnCelda(lunesComida, titulo, urlImagen);
                //ponerRecetaEnCelda(martesCena, "Pizza Casera", "https://example.com/pizza.jpg");

                System.out.println("He encontrado: " + titulo);


            }
        } catch (Exception e) {
            System.err.println("Error al leer el JSON: " + e.getMessage());
        }
    }

    /*
    ///////ESTE ES EL CODIGO VALIDO PARA LA API
    @FXML
    private void manejarBotonBuscar(ActionEvent event) {
        ApiController service = new ApiController();
        try {
            String resultado = service.buscarRecetas("pasta");
            System.out.println(resultado); // Verás los datos en la consola de IntelliJ
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    /*
    private void ponerRecetaEnCelda(VBox celda, String titulo, String urlImagen) {
        // 1. Limpiamos la celda por si ya había algo
        celda.getChildren().clear();
        celda.setSpacing(5); // Espacio entre imagen y texto
        celda.setAlignment(Pos.CENTER);

        // Crear la imagen
        Image img = new Image(urlImagen, true); // El 'true' ayuda a que no se bloquee la app mientras carga
        ImageView iv = new ImageView(img);

        // Ajustar tamaño para que quepa en tu celda
        iv.setFitWidth(100);
        iv.setPreserveRatio(true);

        // 2. Creamos el texto
        Label label = new Label(titulo);
        label.setWrapText(true); // Por si el nombre es muy largo
        label.setStyle("-fx-font-weight: bold; -fx-font-size : 15px; -fx-text-alignment: center;");
        celda.getChildren().addAll(iv, label);

        // 3. Creamos la imagen (usando la URL de Spoonacular)
        try {
            ImageView iv = new ImageView(new Image(urlImagen, 100, 100, true, true));
            celda.getChildren().addAll(iv, label);
        } catch (Exception e) {
            // Si la imagen falla, ponemos solo el texto
            celda.getChildren().add(label);
        }


    }
    */
    public void ponerRecetaEnCelda(VBox celda, String titulo, String urlImg, JsonObject recetaJson) {
        Platform.runLater(() -> {
            try {
                celda.getChildren().clear();

                // --- Contenedor de Capas (StackPane) ---
                StackPane capas = new StackPane();
                capas.setPrefSize(150, 200);

                // --- Capa 1: Contenido (Foto + Título) ---
                VBox contenido = new VBox(5);
                contenido.setAlignment(Pos.CENTER);

                ImageView fotoComida = new ImageView();
                fotoComida.setFitWidth(110);
                fotoComida.setFitHeight(80);
                fotoComida.setPreserveRatio(true);

                if (urlImg != null && !urlImg.isEmpty()) {
                    Image img = new Image(urlImg.replace("http://", "https://"), true);
                    img.errorProperty().addListener((obs, oldV, error) -> {
                        if (error) fotoComida.setImage(new Image(getClass().getResourceAsStream("/imagenes/logo.png")));
                    });
                    fotoComida.setImage(img);
                }

                Label lblTitulo = new Label(titulo);
                lblTitulo.setWrapText(true);
                lblTitulo.setAlignment(Pos.CENTER);
                lblTitulo.setStyle("-fx-font-size: 15px; -fx-text-alignment: center;");
                lblTitulo.setMaxWidth(130);

                contenido.getChildren().addAll(fotoComida, lblTitulo);

                // --- Botón Favorito (Aislado arriba a la derecha) ---
                ToggleButton btnFav = new ToggleButton();
                btnFav.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                Image imgVacio = new Image(getClass().getResourceAsStream("/imagenes/corazon-contorno-rojo.png"));
                Image imgRelleno = new Image(getClass().getResourceAsStream("/imagenes/corazon-relleno-rojo.png"));

                ImageView iconoBtn = new ImageView(imgVacio);
                iconoBtn.setFitWidth(22);
                iconoBtn.setFitHeight(22);
                btnFav.setGraphic(iconoBtn);

                // Posicionamos el botón arriba a la derecha
                StackPane.setAlignment(btnFav, Pos.TOP_RIGHT);
                StackPane.setMargin(btnFav, new Insets(5, 5, 0, 0)); // Margen para que no pegue al borde

                configurarBotonFavorito(btnFav, iconoBtn, imgRelleno, imgVacio, titulo, urlImg);

                // --- Montar todo ---
                capas.getChildren().addAll(contenido, btnFav);
                celda.getChildren().add(capas);
                celda.setAlignment(Pos.CENTER);

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        });
    }

    /// para probar sin usar la api
    public void procesarMenuCompleto(String jsonRespuesta) {
        try {
            JsonObject data = JsonParser.parseString(jsonRespuesta).getAsJsonObject();
            JsonObject week = data.getAsJsonObject("week");

            String[] diasApi = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
            VBox[][] matrizInterfaz = obtenerMatrizCeldas();

            for (int i = 0; i < diasApi.length; i++) {
                System.out.println("Intentando llenar el día: " + diasApi[i]);
                JsonObject diaJson = week.getAsJsonObject(diasApi[i]);
                String nombreDia = diasApi[i];
                diaJson = week.getAsJsonObject(nombreDia);

                if (diaJson != null) {
                    JsonArray comidas = diaJson.getAsJsonArray("meals");

                    for (int j = 0; j < 3; j++) {

                        JsonObject receta = comidas.get(j).getAsJsonObject();

                        // 1. Sacamos el Título con seguridad (si no existe, ponemos "Sin nombre")
                        String titulo = "Receta sin nombre";
                        if (receta.has("title") && !receta.get("title").isJsonNull()) {
                            titulo = receta.get("title").getAsString();
                        }

                        // 2. Sacamos la Imagen con seguridad
                        String urlImg = "";
                        if (receta.has("image") && !receta.get("image").isJsonNull()) {
                            urlImg = receta.get("image").getAsString();
                        } else if (receta.has("id") && !receta.get("id").isJsonNull()) {
                            // Si no hay campo 'image', la construimos con el ID
                            urlImg = "https://spoonacular.com/recipeImages/" + receta.get("id").getAsString() + "-312x231.jpg";
                        }

                        // 3. Pintamos la celda (esto ya lo tenías)
                        ponerRecetaEnCelda(matrizInterfaz[i][j], titulo, urlImg, receta);

                    }
                }
            }
            System.out.println("✅ ¡Menú semanal cargado con éxito!");

        } catch (Exception e) {
            System.err.println("❌ Error crítico en el proceso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /// mientras no puedo usar la api
    private VBox[][] obtenerMatrizCeldas() {
        // Agrupamos los VBox que ya tienes vinculados con @FXML
        return new VBox[][]{
                {lunesDesayuno, lunesComida, lunesCena},       // Día 0
                {martesDesayuno, martesComida, martesCena},     // Día 1
                {miercolesDesayuno, miercolesComida, miercolesCena}, // Día 2
                {juevesDesayuno, juevesComida, juevesCena},     // Día 3
                {viernesDesayuno, viernesComida, viernesCena},    // Día 4
                {sabadoDesayuno, sabadoComida, sabadoCena},      // Día 5
                {domingoDesayuno, domingoComida, domingoCena}    // Día 6
        };
    }

    // esto es para probar xq no me puedo conectar a la api
    @FXML
    private void onBotonGenerarClick(ActionEvent event) {
        Button btn = (Button) event.getSource();

        // 2. Cambios visuales inmediatos
        btn.setText("Cargando...");
        btn.setDisable(true); // Evitamos que el usuario pulse mil veces

        // 3. Crear un hilo para que la base de datos no congele la ventana
        Thread thread = new Thread(() -> {
            try {

                String jsonSimulado = "{" +
                        "  'week': {" +
                        "    'monday': { 'meals': [{ 'title': 'Avena con Frutas', 'image': 'https://spoonacular.com/recipeImages/632660-312x231.jpg' }, { 'title': 'Lentejas Veganas', 'image': 'https://spoonacular.com/recipeImages/649931-312x231.jpg' }, { 'title': 'Ensalada de Quinoa', 'image': 'https://spoonacular.com/recipeImages/657698-312x231.jpg' }] }," +
                        "    'tuesday': { 'meals': [{ 'title': 'Tortilla de Espinacas', 'image': 'https://spoonacular.com/recipeImages/660405-312x231.jpg' }, { 'title': 'Pasta Integral', 'image': 'https://spoonacular.com/recipeImages/654883-312x231.jpg' }, { 'title': 'Sopa de Verduras', 'image': 'https://spoonacular.com/recipeImages/663157-312x231.jpg' }] }," +
                        "    'wednesday': { 'meals': [{ 'title': 'Pisto Manchego', 'image': 'https://spoonacular.com/recipeImages/660405-312x231.jpg' }, { 'title': 'Arroz con Pollo', 'image': 'https://spoonacular.com/recipeImages/654883-312x231.jpg' }, { 'title': 'Crema de Calabaza', 'image': 'https://spoonacular.com/recipeImages/663157-312x231.jpg' }] }," +
                        "    'thursday': { 'meals': [{ 'title': 'Batido Verde', 'image': '...' }, { 'title': 'Garbanzos', 'image': '...' }, { 'title': 'Pescado', 'image': '...' }] }," +
                        "    'friday': { 'meals': [{ 'title': 'Pan con Tomate', 'image': '...' }, { 'title': 'Filete', 'image': '...' }, { 'title': 'Pizza Saludable', 'image': '...' }] }," +
                        "    'saturday': { 'meals': [{ 'title': 'Pancakes', 'image': '...' }, { 'title': 'Hamburguesa Veggie', 'image': '...' }, { 'title': 'Tacos', 'image': '...' }] }," +
                        "    'sunday': { 'meals': [{ 'title': 'Fruta Mix', 'image': '...' }, { 'title': 'Paella', 'image': '...' }, { 'title': 'Sandwich', 'image': '...' }] }" +
                        "  }" +
                        "}";

                procesarMenuCompleto(jsonSimulado);
                Platform.runLater(() -> {
                    btn.setText("Generar Menú");
                    btn.setDisable(false);
                    System.out.println("✅ Menú listo");
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    btn.setText("Generar Menú");
                    btn.setDisable(false);
                });
            }
        });

        thread.setDaemon(true); // Para que el hilo se cierre si cierras la app
        thread.start();
    }


/*
    /// ///este codigo del boton para guardar en la bbdd en favorito cuando funcione la api///////////
    private void configurarBotonFavorito(ToggleButton btnFav, ImageView iconoBtn, Image imgRelleno, Image imgVacio, String titulo, String urlImg, int idApi) {

        // 1. Estado inicial: Consultar si ya es favorito en la BD
        FavoritoDao favDAO = new FavoritoDao();
        int idUsuario = Sesion.getUsuario().getId(); // Ajusta según tu gestión de sesión

        if (favDAO.esFavorito(idUsuario, idApi)) {
            btnFav.setSelected(true);
            iconoBtn.setImage(imgRelleno);
        } else {
            btnFav.setSelected(false);
            iconoBtn.setImage(imgVacio);
        }

        // 2. Evento de clic
        btnFav.setOnAction(e -> {
            try {
                RecetaDao recetaDao = new RecetaDao();
                RecetaDto receta = new RecetaDto(titulo, urlImg, idApi);

                if (btnFav.isSelected()) {
                    iconoBtn.setImage(imgRelleno);
                    int idRecetaLocal = recetaDao.asegurarRecetaEnBD(receta);
                    favDAO.guardarFavorito(idUsuario, idRecetaLocal);
                    System.out.println("❤️ Guardado: " + titulo);
                } else {
                    iconoBtn.setImage(imgVacio);
                    int idRecetaLocal = recetaDao.obtenerIdPorApi(idApi);
                    favDAO.eliminarFavorito(idUsuario, idRecetaLocal);
                    System.out.println("💔 Eliminado: " + titulo);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }*/

    /// / codigo para probar que guarda en la bbdd sin conectar con la api

    private void configurarBotonFavorito(ToggleButton btnFav, ImageView iconoBtn, Image imgRelleno, Image imgVacio, String titulo, String urlImg) {
        try {
            FavoritoDao favoritoDao = new FavoritoDao();
            RecetaDao recetaDao = new RecetaDao();

            // --- GENERAMOS UN ID LOCAL BASADO EN EL TÍTULO ---
            // Como no hay API, inventamos uno único para esta receta
            int idApiFicticio = Math.abs(titulo.hashCode());

            // Obtenemos el ID del usuario (ajusta a tu clase Sesion)
            int idUsuario = Sesion.getUsuario().getId();

            // 1. Estado inicial: ¿Ya estaba en favoritos?
            if (favoritoDao.esFavorito(idUsuario, idApiFicticio)) {
                btnFav.setSelected(true);
                iconoBtn.setImage(imgRelleno);
            } else {
                btnFav.setSelected(false);
                iconoBtn.setImage(imgVacio);
            }

            // 2. Evento al pinchar
            btnFav.setOnAction(e -> {
                try {
                    if (btnFav.isSelected()) {
                        iconoBtn.setImage(imgRelleno);

                        // Creamos el DTO con lo que tenemos
                        RecetaDto dto = new RecetaDto();
                        dto.setTitulo(titulo);
                        dto.setUrlImagen(urlImg);
                        dto.setIdApi(idApiFicticio); // Usamos el inventado

                        int idLocal = recetaDao.asegurarRecetaEnBD(dto);
                        favoritoDao.guardarFavorito(idUsuario, idLocal);
                        System.out.println("❤️ Guardado localmente: " + titulo);
                    } else {
                        iconoBtn.setImage(imgVacio);
                        int idLocal = recetaDao.obtenerIdPorApi(idApiFicticio);
                        if (idLocal != -1) {
                            favoritoDao.eliminarFavorito(idUsuario, idLocal);
                            System.out.println("💔 Eliminado localmente: " + titulo);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            System.err.println("Error al inicializar botón: " + ex.getMessage());
        }
    }
}




