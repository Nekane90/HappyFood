package happyDTO;

public class Ingrediente {
    private int id;
    private String nombre;
    private String categoria;

    public Ingrediente(int id, String nombre, String categoria){
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
    }

    /// getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
