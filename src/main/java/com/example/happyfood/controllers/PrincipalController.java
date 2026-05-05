package com.example.happyfood.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import happyDAO.FavoritoDao;
import happyDAO.PlanificadorSemanalDao;
import happyDAO.RecetaDao;
import happyDTO.RecetaDto;
import happyDTO.UsuarioDto;
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    private static final Map<String, String> MAPA_DIETAS = Map.of(
            "Sin Dieta", "none",
            "Vegana", "vegan",
            "Vegetariana", "vegetarian",
            "Sin Gluten", "gluten-free",
            "Mediterránea", "mediterranean"
    );

    private static final Map<String, String> MAPA_INTOLERANCIAS = Map.of(
            "Lactosa", "lactose",
            "Gluten", "gluten",
            "Frutos Secos", "nuts",
            "Marisco", "shellfish",
            "Huevo", "egg"
    );
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
    @FXML private VBox capaCarga;


    FavoritoDao favoritoDao = new FavoritoDao();
    RecetaDao recetaDao = new RecetaDao();
    private Set<Integer> misFavoritos;
    private VBox celdaSeleccionada;
    private String ultimoJsonRecibido;
    private AtomicInteger platosCargados = new AtomicInteger(0);



    @FXML
    public void initialize() {
        configurarTitulos();
        matrizInterfaz = obtenerMatrizCeldas();
        configurarMenuComun(menuLateral, this);
        //cargarFotoUsuario();
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
    /**
     * Carga o refresca el avatar del usuario actual desde la Sesión.
     */
    public void actualizarAvatarUsuario() {
        UsuarioDto usuario = Sesion.getUsuario();
        if (usuario != null && usuario.getAvatar() != null) {
            cargarImagenEnCirculo(usuario.getAvatar());
        } else {
            // Imagen por defecto si no hay avatar guardado
            cargarImagenEnCirculo("animal_1.png");
        }
    }

    private void cargarImagenEnCirculo(String nombreImagen) {
        try {
            // La ruta DEBE empezar desde donde está la carpeta en resources
            String ruta = "/imagenes/avatares/" + nombreImagen;

            var recurso = getClass().getResource(ruta);

            if (recurso != null) {
                Image img = new Image(recurso.toExternalForm());
                circuloAvatar.setFill(new ImagePattern(img));
            } else {
                // Esto te ayudará a ver en consola qué nombre está fallando exactamente
                System.err.println("No se encontró el archivo: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar avatar: " + e.getMessage());
        }
    }

    /**
     * Método para abrir la ventana de modificar cuenta.
     * Vincula este método al MenuItem "Mi Cuenta" en el FXML.
     */
    @FXML
    public void abrirMiCuenta() {
        try {
            // Asegúrate de que el nombre del FXML sea exactamente el mismo que tienes en tu carpeta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/modificar_usuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Configuración de mi cuenta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Usamos showAndWait para que, al cerrar la ventana de edición,
            // el código de abajo se ejecute y refresque el avatar.
            stage.showAndWait();
            actualizarAvatarUsuario();

        } catch (IOException e) {
            System.err.println("No se pudo abrir la pantalla de cuenta: " + e.getMessage());
            e.printStackTrace();
        }
    }




    // --- EL BOTÓN PRINCIPAL DE GENERAR MENÚ ---
    @FXML
    private void onBotonGenerarClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        btn.setText("Cargando Menú...");
        btn.setDisable(true);
        capaCarga.setVisible(true);

        String dietaApi = Sesion.getUsuario().getTipoDieta();
        String intoleranciasApi = Sesion.getUsuario().getIntolerancias();

        // Debug para que tú mismo veas si la sesión tiene los datos antes de llamar a la API
        System.out.println("DEBUG SESIÓN - Dieta: " + dietaApi + " | Intolerancias: " + intoleranciasApi);

        Thread thread = new Thread(() -> {
            try {
                ApiController api = new ApiController();

                // Enviamos a la API
                String resultadoJson = api.obtenerPlanSemanal(dietaApi, intoleranciasApi);

                System.out.println("RESPUESTA DE LA API: " + resultadoJson);

                // Validación de JSON (lo que añadimos antes para evitar el crash)
                if (resultadoJson == null || !resultadoJson.trim().startsWith("{")) {
                    Platform.runLater(() -> {
                        capaCarga.setVisible(false);
                        btn.setText("Generar Nuevo Menú");
                        btn.setDisable(false);
                        mostrarAlerta("Servicio no disponible", "La API está en mantenimiento o la respuesta es inválida.");
                    });
                    return;
                }

                this.ultimoJsonRecibido = resultadoJson;

                Platform.runLater(() -> {
                    procesarMenuCompleto(resultadoJson, misFavoritos);
                    btn.setText("Generar Nuevo Menú");
                    btn.setDisable(false);
                    // Nota: capaCarga se oculta dentro de procesarMenuCompleto al llegar a 21
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    capaCarga.setVisible(false);
                    mostrarAlerta("Error", "Error de conexión: " + e.getMessage());
                    btn.setText("Generar Nuevo Menú");
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
            platosCargados.set(0);
            capaCarga.setVisible(true);

            JsonObject data = JsonParser.parseString(jsonRespuesta).getAsJsonObject();
            JsonObject week = data.has("week") ? data.getAsJsonObject("week") : data;
            String[] diasApi = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

            for (int i = 0; i < diasApi.length; i++) {
                JsonObject diaJson = week.getAsJsonObject(diasApi[i]);
                if (diaJson == null){
                    platosCargados.addAndGet(3);
                    continue;
                }
                JsonArray comidas = diaJson.getAsJsonArray("meals");

                for (int j = 0; j < 3; j++) {
                    if (j >= comidas.size()) {
                        platosCargados.incrementAndGet();
                        continue;
                    }

                    JsonObject receta = comidas.get(j).getAsJsonObject();
                    int idApiReal = receta.get("id").getAsInt();
                    String tituloOriginal = receta.get("title").getAsString();
                    String urlImg = "https://spoonacular.com/recipeImages/" + idApiReal + "-312x231.jpg";
                    VBox celda = matrizInterfaz[i][j];

                    // --- Dibujar la receta con un pequeño respiro para que el GIF no se congele ---
                    int finalI = i; int finalJ = j;
                    Thread hiloDibujo = new Thread(() -> {
                        try {
                            // Esperamos un poquito entre plato y plato (30ms) para dejar que el GIF se mueva
                            Thread.sleep((finalI * 3 + finalJ) * 30);
                            Platform.runLater(() -> {
                                ponerRecetaEnCelda(celda, tituloOriginal, urlImg, receta, misFavoritos, idApiReal);
                            });
                        } catch (InterruptedException e) { e.printStackTrace(); }
                    });
                    hiloDibujo.setDaemon(true);
                    hiloDibujo.start();

                    // ---  Traducción ---
                    Thread t = new Thread(() -> {
                        try {
                            // El delay de traducción debe ser mayor que el de dibujo
                            Thread.sleep((finalI * 3 + finalJ) * 150);
                            String traducido = TraductorService.traducirFrase(tituloOriginal);

                            Platform.runLater(() -> {
                                Label lb = buscarLabelEnCelda(celda);
                                if (lb != null) lb.setText(traducido);

                                int total = platosCargados.incrementAndGet();
                                if (total >= 21) {
                                    capaCarga.setVisible(false);
                                    System.out.println("✅ Menú listo. GIF ocultado.");
                                }
                            });
                        } catch (Exception e) {
                            if (platosCargados.incrementAndGet() >= 21) {
                                Platform.runLater(() -> capaCarga.setVisible(false));
                            }
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                }
            }
        } catch (Exception e) {
            capaCarga.setVisible(false);
            e.printStackTrace();
        }
    }
    //poner la receta en la celda
    public void ponerRecetaEnCelda(VBox celda, String titulo, String urlImg, JsonObject recetaJson, Set<Integer> misFavoritos, int idApiReal) {
        celda.getChildren().clear();

        StackPane capas = new StackPane();
        VBox contenido = new VBox(5);
        contenido.setAlignment(Pos.CENTER);

        ImageView fotoComida = new ImageView(new Image(urlImg, 110, 80, true, true, true));
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

            //  Actualización de datos en segundo plano (Silencioso)
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
        //abrirHistorial(this);
    }

    //metodo para actualizar solo el corazon de la celda que se ha cambiado
    private void actualizarCorazonCelda(boolean ahoraEsFav) {
        try {
            // Entramos al StackPane de la celda que guardamos al hacer clic
            StackPane stack = (StackPane) celdaSeleccionada.getChildren().get(0);

            //  Buscamos el ToggleButton entre sus hijos
            for (javafx.scene.Node nodo : stack.getChildren()) {
                if (nodo instanceof ToggleButton) {
                    ToggleButton btn = (ToggleButton) nodo;

                    // Sincronizamos el estado del botón
                    btn.setSelected(ahoraEsFav);

                    // Cambiamos la imagen del corazón
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
        Platform.runLater(() -> capaCarga.setVisible(true));

        Thread t = new Thread(() -> {
            try {
                PlanificadorSemanalDao dao = new PlanificadorSemanalDao();
                String ultimoJson = dao.obtenerUltimoPlan(idUsuario);

                if (ultimoJson != null) {
                    this.ultimoJsonRecibido = ultimoJson;

                    // 1. Ejecutamos el procesado en el hilo de la UI
                    Platform.runLater(() -> {
                        // Procesamos el menú (esto crea los nodos visuales)
                        procesarMenuCompleto(ultimoJson, misFavoritos);
                        System.out.println("✅ Interfaz lista y spinner oculto.");
                    });
                } else {
                    Platform.runLater(() -> capaCarga.setVisible(false));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> capaCarga.setVisible(false));
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
    //boton mis menus
    @FXML
    private void abrirHistorialMenus(ActionEvent event) {
        try {
            Button btn = (Button) event.getSource();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/historialMenu.fxml"));
            Parent root = loader.load();
            HistorialMenuController controller = loader.getController();
            controller.setMainController(this); // Pasamos la referencia para que pueda cargar el JSON

            Stage stage = new Stage();
            stage.setTitle("Mis Menús Guardados");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String obtenerDietaApi(String dietaBBDD) {
        // Si no hay dieta o es "Sin Dieta", devolvemos cadena vacía
        if (dietaBBDD == null || !MAPA_DIETAS.containsKey(dietaBBDD)) return "";
        return MAPA_DIETAS.get(dietaBBDD);
    }

    private String obtenerIntoleranciaApi(String intoleranciaBBDD) {
        if (intoleranciaBBDD == null || !MAPA_INTOLERANCIAS.containsKey(intoleranciaBBDD)) return "";
        return MAPA_INTOLERANCIAS.get(intoleranciaBBDD);
    }
    private String obtenerIntoleranciasMultiplesApi(String intoleranciasBBDD) {
        if (intoleranciasBBDD == null || intoleranciasBBDD.isEmpty()) return "";

        // 1. Separamos por comas (por si vienen varias: "Lactosa, Gluten")
        String[] partes = intoleranciasBBDD.split(",");
        StringBuilder resultado = new StringBuilder();

        for (String parte : partes) {
            String limpia = parte.trim(); // Quitamos espacios en blanco
            if (MAPA_INTOLERANCIAS.containsKey(limpia)) {
                if (resultado.length() > 0) resultado.append(","); // Añadimos coma entre medias
                resultado.append(MAPA_INTOLERANCIAS.get(limpia));
            }
        }

        return resultado.toString(); // Devolverá algo como "lactose,gluten"
    }

    @FXML
    private void abrirNevera(ActionEvent event) {
        try {
            Button btn = (Button) event.getSource();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/nevera.fxml"));
            Parent root = loader.load();
            NeveraController controller = loader.getController();
            controller.setMainController(this); // Pasamos la referencia para que pueda cargar el JSON

            Stage stage = new Stage();
            stage.setTitle("Nevera");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    //abrir pantalla mis favoritos
    @FXML
    public void abrirMisFavoritos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/happyfood/mis_favoritos.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Creamos la escena respetando el contenido
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Forzamos el redibujado
            stage.setMaximized(false); // Truco para resetear el estado
            stage.setMaximized(true);

            stage.setTitle("Mis Favoritos");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




