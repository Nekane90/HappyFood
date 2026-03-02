package happyDTO;

import java.util.Date;

public class PlanificadorSemanal {

    private int id;
    private int idUsuario;
    private int idReceta;
    private Date fecha;
    private String tipoComida;//desatuno-comida-cena

    public PlanificadorSemanal(int id, int idUsuario, int idReceta, Date fecha, String tipoComida){
        this.id = id;
        this.idUsuario = idUsuario;
        this.idReceta = idReceta;
        this.fecha = fecha;
        this.tipoComida = tipoComida;
    }

    /// getters y setters

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }
}
