package happyDTO;

import java.util.List;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String email;
    private String password;
    private List<String> intolerancias;
    private String tipoDieta;

    public Usuario(int id, String nombreUsuario, String email, String password, List<String> intolerancias, String tipoDieta){
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.intolerancias = intolerancias;
        this.tipoDieta = tipoDieta;
    }

    public Usuario (int id, List<String> intolerancias, String tipoDieta ){
        this.id = id;
        this.intolerancias = intolerancias;
        this.tipoDieta = tipoDieta;
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

    public List<String> getIntolerancias() {
        return intolerancias;
    }

    public void setIntolerancias(List<String> intolerancias) {
        this.intolerancias = intolerancias;
    }

    public String getTipoDieta() {
        return tipoDieta;
    }

    public void setTipoDieta(String tipoDieta) {
        this.tipoDieta = tipoDieta;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
