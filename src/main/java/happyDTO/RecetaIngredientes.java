package happyDTO;

public class RecetaIngredientes {
    private int idReceta;
    private int idIngrediente;
    private int cantidad;
    private String unidadMedida;


public RecetaIngredientes(int idReceta, int idIngrediente, int cantidad, String unidadMedida){
    this.idReceta = idReceta;
    this.idIngrediente = idIngrediente;
    this.cantidad = cantidad;
    this.unidadMedida = unidadMedida;
}

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public int getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(int idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
