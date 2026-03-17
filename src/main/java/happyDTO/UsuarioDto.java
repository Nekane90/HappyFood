package happyDTO;

import java.util.List;

public class UsuarioDto {
    private int id;
    private String nombreUsuario;
    private String email;
    private String password;
    private String intolerancias;
    private String tipoDieta;
    private String avatar;

    public UsuarioDto(int id, String nombreUsuario, String email, String password, String intolerancias, String tipoDieta){
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.intolerancias = intolerancias;
        this.tipoDieta = tipoDieta;
    }

    public UsuarioDto(int id, String intolerancias, String tipoDieta, String nombreUsuario ){
        this.id = id;
        this.intolerancias = intolerancias;
        this.tipoDieta = tipoDieta;
        this.nombreUsuario = nombreUsuario;
    }

    public UsuarioDto() {

    }


    /// getters y setters
    public int getId(){
        return  id;
    }
    public void setId( int id){
        this.id = id;
    }

    public String getNombreUsuario(){return nombreUsuario;}

    public void setNombreUsuario(String nombreUsuario){
        this.nombreUsuario = nombreUsuario;

    }

    public String getEmail(){return email;}

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){return password;}

    public void setPassword(String password){
        this.password = password;
    }

    public String getIntolerancias() {
        return intolerancias;
    }

    public void setIntolerancias(String intolerancias) {
        this.intolerancias = intolerancias;
    }

    public String getTipoDieta() {
        return tipoDieta;
    }

    public void setTipoDieta(String tipoDieta) {
        this.tipoDieta = tipoDieta;
    }

    //getter
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
