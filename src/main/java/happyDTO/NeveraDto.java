package happyDTO;

public class NeveraDto {
    private int idUsuario;
    private int idIngrediente;
    private int cantidadDisponible;

    public NeveraDto(int idUsuario, int idIngrediente, int cantidadDisponible){
        this.idUsuario = idUsuario;
        this.idIngrediente = idIngrediente;
        this.cantidadDisponible = cantidadDisponible;
    }

    /// getters y setters

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(int idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
}
