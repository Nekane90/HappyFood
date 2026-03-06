package com.example.happyfood.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import happyDAO.FavoritoDao;
import happyDAO.RecetaDao;
import happyDTO.RecetaDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Set;


public class PrincipalController  {
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
    private Circle circuloAvatar;
    @FXML
    VBox[][] matrizInterfaz;

    FavoritoDao favoritoDao = new FavoritoDao();
    RecetaDao recetaDao = new RecetaDao();


    @FXML
    public void initialize() {
        configurarTitulos();
        // botonCorazon();
        matrizInterfaz = obtenerMatrizCeldas();
        //cargarFotoUsuario();
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
    /* ESTO ES MIOOO(MAIALEN) Q LO TENGO Q TERMINAR
        public void cargarFotoUsuario() {
        // Supongamos que tienes el ResultSet del usuario que acaba de entrar
        try {
            String fotoBD = resultSet.getString("imagen");

            // Construimos la ruta.
            // IMPORTANTE: Verifica que la ruta empiece por "/" y sea exacta
            String ruta = "/com/example/happyfood/imagenes/avatares/" + fotoBD;

            InputStream is = getClass().getResourceAsStream(ruta);

            if (is != null) {
                Image img = new Image(is);
                circuloAvatar.setFill(new ImagePattern(img));
            } else {
                System.out.println("No se pudo encontrar la imagen: " + ruta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
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
    public void ponerRecetaEnCelda(VBox celda, String titulo, String urlImg, JsonObject recetaJson,Set<Integer> misFavoritos) {
        Platform.runLater(() -> {
            try {
                celda.getChildren().clear();
                int idApi = generarIdFicticio(titulo);

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
                    Image img = new Image(urlImg.replace("http://", "https://"), 110, 80, true, true, true);
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

                // Calcula si es favorito
                boolean esFav = (misFavoritos != null && misFavoritos.contains(idApi));
                if(esFav){
                    btnFav.setSelected(true);
                    iconoBtn.setImage(imgRelleno);
                } else {
                btnFav.setSelected(false);
                iconoBtn.setImage(imgVacio);
                }


                configurarBotonFavorito(btnFav, iconoBtn, imgRelleno, imgVacio, titulo, urlImg,esFav);

                // --- Montar todo ---
                capas.getChildren().addAll(contenido, btnFav);
                celda.getChildren().add(capas);
                celda.setAlignment(Pos.CENTER);

                //hacemos un escuchador de las celdas para que al clickar se abra otra pantalla con la receta
                celda.setCursor(Cursor.HAND); // Que el ratón cambie a mano para saber que es clicable
                celda.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 1) { // Un solo clic
                        abrirDetalleReceta(titulo, urlImg, recetaJson);
                    }
                });

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        });
    }

    /// para probar sin usar la api
    public void procesarMenuCompleto(String jsonRespuesta, Set<Integer> misFavoritos) {
        try {
            JsonObject data = JsonParser.parseString(jsonRespuesta).getAsJsonObject();
            JsonObject week = data.getAsJsonObject("week");
            String[] diasApi = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

            for (int i = 0; i < diasApi.length; i++) {
                JsonObject diaJson = week.getAsJsonObject(diasApi[i]);
                if (diaJson != null) {
                    JsonArray comidas = diaJson.getAsJsonArray("meals");

                    // --- MEJORA: Preparamos los datos del día en el Hilo Secundario ---
                    // (Para no perder tiempo dentro del hilo visual)
                    final int filaDia = i; // Necesario para usarlo en lambda

                    // Procesamos los datos de las 3 comidas antes de ir a la interfaz
                    for (int j = 0; j < 3; j++) {
                        JsonObject receta = comidas.get(j).getAsJsonObject();
                        String titulo = receta.has("title") ? receta.get("title").getAsString() : "Sin nombre";
                        String urlImg = receta.has("image") ? receta.get("image").getAsString() :
                                "https://spoonacular.com/recipeImages/" + receta.get("id").getAsString() + "-312x231.jpg";

                        // Llamamos a ponerRecetaEnCelda tal cual la tienes
                        // Pero fíjate que ya no hay nada pesado de lógica aquí
                        ponerRecetaEnCelda(matrizInterfaz[filaDia][j], titulo, urlImg, receta, misFavoritos);
                        Thread.sleep(10);
                    }
                }
            }
        } catch (Exception e) {
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
                // --- PASO A: BUSCAR FAVORITOS EN LA BBDD (UNA SOLA VEZ) ---
                FavoritoDao favoritoDao = new FavoritoDao();
                // Obtenemos el ID del usuario de tu clase Sesion
                int idUsuario = Sesion.getUsuario().getId();

                // Traemos todos los IDs de golpe a la memoria (al Set)
                Set<Integer> misFavoritos = favoritoDao.obtenerIdsFavoritos(idUsuario);
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

                procesarMenuCompleto(jsonSimulado,misFavoritos);
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

    private void configurarBotonFavorito(ToggleButton btnFav, ImageView iconoBtn, Image imgRelleno, Image imgVacio, String titulo, String urlImg, boolean yaEsFavorito) {
        try {
            btnFav.setSelected(yaEsFavorito);
            iconoBtn.setImage(yaEsFavorito ? imgRelleno : imgVacio);

            //  Evento al pinchar
            btnFav.setOnAction(e -> {
                try {

                    // --- GENERAMOS UN ID LOCAL BASADO EN EL TÍTULO ---
                    // Como no hay API, inventamos uno único para esta receta
                    int idApiFicticio = generarIdFicticio(titulo);

                    // Obtenemos el ID del usuario (ajusta a tu clase Sesion)
                    int idUsuario = Sesion.getUsuario().getId();
                    if (btnFav.isSelected()) {
                        iconoBtn.setImage(imgRelleno);
                    } else {
                    iconoBtn.setImage(imgVacio);
                    }
                    Thread t = new Thread(() -> {
                        try {
                            if (btnFav.isSelected()) {
                                RecetaDto dto = new RecetaDto(titulo, urlImg, idApiFicticio);
                                int idLocal = recetaDao.asegurarRecetaEnBD(dto);
                                favoritoDao.guardarFavorito(idUsuario, idLocal);

                            } else {
                                int idLocal = recetaDao.obtenerIdPorApi(idApiFicticio);
                                if (idLocal != -1) {
                                    favoritoDao.eliminarFavorito(idUsuario, idLocal);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                });

        } catch (Exception ex) {
            System.err.println("Error al inicializar botón: " + ex.getMessage());
        }
    }

    /// /aqui abrimos la pantalla del detalle de la receta
    private void abrirDetalleReceta(String titulo, String urlImg, JsonObject recetaJson) {
        try {
            // 1. Cargar el FXML de la nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/detalle_receta.fxml"));
            Parent root = loader.load();

            // 2. Obtener el controlador de la ventana de detalle
            DetalleRecetaController controller = loader.getController();

            controller.initData(titulo, urlImg, recetaJson);

            // 4. Crear el escenario (Stage) y mostrarlo
            Stage stage = new Stage();
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/example/happyfood/estilos.css").toExternalForm());

            stage.setTitle("Preparación: " + titulo);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setMaximized(true);
            // Hacerla modal
            //stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();



        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /// para crear el "id de a api" mientras no puedo conectarme a ella
    private int generarIdFicticio(String titulo) {
        return Math.abs(titulo.toLowerCase().trim().hashCode());
    }
}




