package happyDTO;

public class FavoritoDto {

    private int id;
    private int idUsuario;
    private int idReceta;

    public FavoritoDto(int id, int idUsuario, int idReceta){
        this.id = id;
        this.idUsuario = idUsuario;
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

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }
}
