package happyDTO;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class PlanificadorSemanalDto {

    private int id;
    private int idUsuario;
    private Timestamp fecha;
    private  String nombre_menu;
    private String json;


    public PlanificadorSemanalDto(int id, int idUsuario, Timestamp fecha, String nombreMenu, String json){
        this.id = id;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.nombre_menu = nombreMenu;
        this.json = json;

    }
    public PlanificadorSemanalDto(int id, String nombre_menu,String json, Timestamp fecha){
        this.id = id;
        this.nombre_menu = nombre_menu;
        this.json = json;
        this.fecha = fecha;

    }

    public PlanificadorSemanalDto() {

    }


    /// getters y setters

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getNombre_menu() {
        return nombre_menu;
    }

    public void setNombre_menu(String nombre_menu) {
        this.nombre_menu = nombre_menu;
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
        this.fecha = (Timestamp) fecha;
    }


}
