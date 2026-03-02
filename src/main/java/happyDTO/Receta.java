package happyDTO;

public class Receta {
    private int id;
    private String titulo;
    private String instrucciones;
    private  int tiempoPreparacion;
    private String dificultad;
    private String urlImagen;
    private int idApi;

    //constructor

    public Receta(int id, String titulo, String instrucciones, int tiempoPreparacion, String dificultad, String urlImagen, int idApi ){
        this.id = id;
        this.titulo = titulo;
        this.instrucciones = instrucciones;
        this.tiempoPreparacion = tiempoPreparacion;
        this.dificultad = dificultad;
        this.urlImagen = urlImagen;
        this.idApi = idApi;
    }

    /// getters y setters
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public void setTiempoPreparacion(int tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public int getIdApi() {
        return idApi;
    }

    public void setIdApi(int idApi) {
        this.idApi = idApi;
    }
}
