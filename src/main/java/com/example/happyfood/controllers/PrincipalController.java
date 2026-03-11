package com.example.happyfood.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import happyDAO.FavoritoDao;
import happyDAO.PlanificadorSemanalDao;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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
import java.util.List;
import java.util.Set;


public class PrincipalController extends  MenuLateralController  {
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
    @FXML private MenuButton menuLateral;

    FavoritoDao favoritoDao = new FavoritoDao();
    RecetaDao recetaDao = new RecetaDao();
    private Set<Integer> misFavoritos;
    private VBox celdaSeleccionada;
    private String ultimoJsonRecibido;


    @FXML
    public void initialize() {
        configurarTitulos();
        matrizInterfaz = obtenerMatrizCeldas();
        configurarMenuComun(menuLateral, this);

        // Cargar favoritos al inicio
        new Thread(() -> {
            this.misFavoritos = favoritoDao.obtenerIdsFavoritos(Sesion.getUsuario().getId());
            cargarUltimoMenuSiExiste();
        }).start();
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

    // --- EL BOTÓN PRINCIPAL DE GENERAR MENÚ ---
    @FXML
    private void onBotonGenerarClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        btn.setText("Cargando Menú...");
        btn.setDisable(true);

        Thread thread = new Thread(() -> {
            try {

                ApiController api = new ApiController();
                String resultadoJson = api.obtenerPlanSemanal();

                this.ultimoJsonRecibido = resultadoJson; // Guardamos para el botón de "Guardar"

                // 2. PROCESAR EN INTERFAZ
                Platform.runLater(() -> {
                    procesarMenuCompleto(resultadoJson, misFavoritos);
                    btn.setText("Generar Nuevo Menú");
                    btn.setDisable(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    mostrarAlerta("Error de Conexión", "No se pudo conectar con la API de Spoonacular.");
                    btn.setDisable(false);
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    // --- PROCESAR EL JSON  DE SPOONACULAR ---
    public void procesarMenuCompleto(String jsonRespuesta, Set<Integer> misFavoritos) {
        try {
            JsonObject data = JsonParser.parseString(jsonRespuesta).getAsJsonObject();
            // Usamos la protección que hablamos antes por si no viene el nodo "week"
            JsonObject week = data.has("week") ? data.getAsJsonObject("week") : data;
            String[] diasApi = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

            for (int i = 0; i < diasApi.length; i++) {
                JsonObject diaJson = week.getAsJsonObject(diasApi[i]);
                if (diaJson == null) continue;

                JsonArray comidas = diaJson.getAsJsonArray("meals");

                for (int j = 0; j < 3; j++) {
                    if (j >= comidas.size()) break;

                    JsonObject receta = comidas.get(j).getAsJsonObject();

                    int idApiReal = receta.has("id") ? receta.get("id").getAsInt() : 0;
                    String tituloOriginal = receta.has("title") ? receta.get("title").getAsString() : "No title";
                    String urlImg = "https://spoonacular.com/recipeImages/" + idApiReal + "-312x231.jpg";

                    VBox celda = matrizInterfaz[i][j]; // La celda donde irá la receta

                    Platform.runLater(() -> {
                        ponerRecetaEnCelda(celda, tituloOriginal, urlImg, receta, misFavoritos, idApiReal);
                    });

                    // Traducir en segundo plano con un pequeño retraso para no bloquear Google
                    int delay = (i * 3 + j) * 200; // Crea una cola (0ms, 200ms, 400ms...)

                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep(delay); // Esperamos un poco antes de pedir la traducción
                            String traducido = TraductorService.traducirFrase(tituloOriginal);
                            System.out.println("Original: " + tituloOriginal + " -> Traducido: " + traducido);

                            Platform.runLater(() -> {
                                // Buscamos el Label en el VBox (suponiendo que es el primer o segundo elemento)
                                Label lb = buscarLabelEnCelda(celda);

                                if (lb != null) {
                                    lb.setText(traducido);
                                } else {
                                    System.err.println("¡No se encontró el Label! Estructura de la celda: " + celda.getChildren());
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //poner la receta en la celda
    public void ponerRecetaEnCelda(VBox celda, String titulo, String urlImg, JsonObject recetaJson, Set<Integer> misFavoritos, int idApiReal) {
        celda.getChildren().clear();

        StackPane capas = new StackPane();
        VBox contenido = new VBox(5);
        contenido.setAlignment(Pos.CENTER);

        // Foto e Imagen (Mantén tu lógica de ImageView)
        ImageView fotoComida = new ImageView(new Image(urlImg, 110, 80, true, true));
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(120);

        contenido.getChildren().addAll(fotoComida, lblTitulo);

        // Botón Favorito
        ToggleButton btnFav = new ToggleButton();
        ImageView iconoBtn = new ImageView();
        iconoBtn.setFitWidth(20);
        iconoBtn.setFitHeight(20);

        // Lógica de favoritos corregida con ID Real
        boolean esFav = (misFavoritos != null && misFavoritos.contains(idApiReal));
        Image imgRelleno = new Image(getClass().getResourceAsStream("/imagenes/corazon-relleno-rojo.png"));
        Image imgVacio = new Image(getClass().getResourceAsStream("/imagenes/corazon-contorno-rojo.png"));

        btnFav.setSelected(esFav);
        iconoBtn.setImage(esFav ? imgRelleno : imgVacio);
        btnFav.setGraphic(iconoBtn);
        btnFav.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        // Evento Favorito usando ID Real
        btnFav.setOnAction(e -> {
            manejarAccionFavorito(btnFav, idApiReal, titulo, urlImg, iconoBtn, imgRelleno, imgVacio);
        });

        StackPane.setAlignment(btnFav, Pos.TOP_RIGHT);
        capas.getChildren().addAll(contenido, btnFav);
        celda.getChildren().add(capas);
        celda.setUserData(idApiReal); // Guardamos el ID real en la celda

        celda.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                this.celdaSeleccionada = celda;
                int idReal = (int) celda.getUserData();

                // Hilo para no bloquear la UI mientras descarga las instrucciones
                Thread t = new Thread(() -> {
                    try {
                        ApiController api = new ApiController();
                        String detallesJson = api.obtenerDetallesReceta(idReal);
                        JsonObject recetaCompleta = JsonParser.parseString(detallesJson).getAsJsonObject();

                        Platform.runLater(() -> {
                            abrirDetalleReceta(titulo, urlImg, recetaCompleta, btnFav.isSelected());
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                t.start();
            }
        });
    }


    // --- NUEVO MÉTODO PARA GUARDAR FAVORITO (Limpio) ---
    private void manejarAccionFavorito(ToggleButton btn, int idApi, String titulo, String url, ImageView icono, Image rell, Image vac) {
        int idUsuario = Sesion.getUsuario().getId();
        new Thread(() -> {
            try {
                if (btn.isSelected()) {
                    Platform.runLater(() -> icono.setImage(rell));
                    RecetaDto dto = new RecetaDto(titulo, url, idApi);
                    int idLocal = recetaDao.asegurarRecetaEnBD(dto);
                    favoritoDao.guardarFavorito(idUsuario, idLocal);
                    this.misFavoritos.add(idApi);
                } else {
                    Platform.runLater(() -> icono.setImage(vac));
                    int idLocal = recetaDao.obtenerIdPorApi(idApi);
                    favoritoDao.eliminarFavorito(idUsuario, idLocal);
                    this.misFavoritos.remove(idApi);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }


    /// /aqui abrimos la pantalla del detalle de la receta
    private void abrirDetalleReceta(String titulo, String urlImg, JsonObject recetaJson,boolean favorito) {
        try {
            // Cargar el FXML de la nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/detalle_receta.fxml"));
            Parent root = loader.load();

            //  Obtener el controlador de la ventana de detalle
            DetalleRecetaController controller = loader.getController();

            controller.initData(titulo, urlImg, recetaJson,favorito);

            // Crear el escenario (Stage) y mostrarlo
            Stage stage = new Stage();
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/com/example/happyfood/estilos.css").toExternalForm());

            stage.setTitle("Preparación: " + titulo);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setMaximized(true);
            // Hacerla modal
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            boolean estadoFinal = controller.isEsFavorito();

            // ACTUALIZACIÓN VISUAL INMEDIATA
            if (celdaSeleccionada != null) {
                actualizarCorazonCelda(estadoFinal);
            }

            // 3. Actualización de datos en segundo plano (Silencioso)
            Thread t = new Thread(() -> {
                this.misFavoritos = favoritoDao.obtenerIdsFavoritos(Sesion.getUsuario().getId());
            });
            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // --- GUARDAR EL MENÚ EN EL HISTORIAL ---
    @FXML
    private void botonGuardar() {
        if (ultimoJsonRecibido == null) {
            mostrarAlerta("Error", "No hay ningún menú generado para guardar.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("Mi Menú de la Semana");
        dialog.setTitle("Guardar Planificación");
        dialog.showAndWait().ifPresent(nombre -> {
            if (!nombre.trim().isEmpty()) {
                PlanificadorSemanalDao dao = new PlanificadorSemanalDao();
                boolean ok = dao.guardarPlan(Sesion.getUsuario().getId(), nombre, ultimoJsonRecibido);
                if (ok) System.out.println("Plan guardado");
            }
        });
    }


    //metodo para mostrar alerta (messagebox)
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }



    public Set<Integer> getMisFavoritos() {
        return this.misFavoritos;
    }

    //Con este metodo se llama para abrir el menu desplegable de los menus Como PrincipalController hereda de BaseController,
    // tiene acceso directo al método abrirHistorial
    @FXML
    private void desplegableMisMenus(ActionEvent event) {
        abrirHistorial(this);
    }

    //metodo para actualizar solo el corazon de la celda que se ha cambiado
    private void actualizarCorazonCelda(boolean ahoraEsFav) {
        try {
            // 1. Entramos al StackPane de la celda que guardamos al hacer clic
            StackPane stack = (StackPane) celdaSeleccionada.getChildren().get(0);

            // 2. Buscamos el ToggleButton entre sus hijos
            for (javafx.scene.Node nodo : stack.getChildren()) {
                if (nodo instanceof ToggleButton) {
                    ToggleButton btn = (ToggleButton) nodo;

                    // 3. Sincronizamos el estado del botón
                    btn.setSelected(ahoraEsFav);

                    // 4. Cambiamos la imagen del corazón
                    ImageView icono = (ImageView) btn.getGraphic();
                    String ruta = ahoraEsFav ? "/imagenes/corazon-relleno-rojo.png" : "/imagenes/corazon-contorno-rojo.png";

                    // Usamos Platform.runLater para asegurar que el cambio visual sea fluido
                    Platform.runLater(() -> {
                        icono.setImage(new Image(getClass().getResourceAsStream(ruta)));
                    });

                    break; // Ya lo encontramos, salimos del bucle
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo actualizar el corazón visualmente: " + e.getMessage());
        }
    }
    //metodo que carga en la pantalla principal el ultimo menu existente
    private void cargarUltimoMenuSiExiste() {
        int idUsuario = Sesion.getUsuario().getId();

        // Lo ejecutamos en un hilo para que la app no tarde en abrirse
        Thread t = new Thread(() -> {
            PlanificadorSemanalDao dao = new PlanificadorSemanalDao();
            String ultimoJson = dao.obtenerUltimoPlan(idUsuario);

            if (ultimoJson != null) {
                this.ultimoJsonRecibido = ultimoJson;

                Platform.runLater(() -> {
                    procesarMenuCompleto(ultimoJson, misFavoritos);
                    System.out.println("✅ Último menú cargado por defecto.");
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private Label buscarLabelEnCelda(Parent contenedor) {
        for (javafx.scene.Node nodo : contenedor.getChildrenUnmodifiable()) {
            if (nodo instanceof Label) {
                return (Label) nodo;
            } else if (nodo instanceof Parent) {
                // Si el nodo es otro contenedor (como un StackPane), buscamos dentro
                Label encontrado = buscarLabelEnCelda((Parent) nodo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}




