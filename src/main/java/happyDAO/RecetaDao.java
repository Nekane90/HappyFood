package happyDAO;

import com.example.happyfood.conexion.ConexionDB;
import happyDTO.RecetaDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecetaDao {

    /**
     * Comprueba si la receta de la API ya está en nuestra BD.
     * Si no está, la inserta. En ambos casos, devuelve el ID de NUESTRA base de datos.
     */
    public int asegurarRecetaEnBD(RecetaDto recetaAPI) {
        int idLocal = -1;

        // 1. Intentamos buscar si ya existe por el ID de la API
        String sqlBusqueda = "SELECT id FROM recetas WHERE id_api = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement psBusqueda = con.prepareStatement(sqlBusqueda)) {

            psBusqueda.setInt(1, recetaAPI.getIdApi()); // El ID de Spoonacular
            ResultSet rs = psBusqueda.executeQuery();

            if (rs.next()) {
                // YA EXISTE:recuperamos nuestro ID interno
                idLocal = rs.getInt("id");
            } else {
                // NO EXISTE: Hay que insertarla primero
                idLocal = insertarRecetaNueva(recetaAPI);
            }

        } catch (SQLException e) {
            System.err.println("Error al asegurar receta: " + e.getMessage());
        }

        return idLocal;
    }

    /**
     * Inserta los datos de la API en nuestra tabla local y devuelve el ID generado.
     */
    private int insertarRecetaNueva(RecetaDto r) {
        // Corregido: Quitada la coma extra al final y alineados los 6 campos con los 6 interrogantes
        String sqlInsert = "INSERT INTO recetas (titulo, instrucciones, tiempo_preparacion, dificultad, imagen_url, id_api) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sqlInsert)) {

            ps.setString(1, r.getTitulo());
            ps.setString(2, r.getInstrucciones());
            ps.setInt(3, r.getTiempoPreparacion());
            ps.setString(4, r.getDificultad());
            ps.setString(5, r.getUrlImagen());
            ps.setInt(6, r.getIdApi());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar receta nueva: " + e.getMessage());
        }
        return -1;
    }

    public int obtenerIdPorApi(int idApi) {
        String sql = "SELECT id FROM recetas WHERE id_api = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idApi);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id"); // Encontramos el ID local
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ID por API: " + e.getMessage());
        }

        return -1; // No existe en nuestra base de datos
    }

    public void eliminarFavorito(int idUsuario, int idReceta) {
        // Usamos el ID de usuario y el ID de NUESTRA receta en la BD
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_receta = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idReceta);

            int filasBorradas = ps.executeUpdate();

            if (filasBorradas > 0) {
                System.out.println("✅ Favorito eliminado de la base de datos.");
            } else {
                System.out.println("⚠️ No se encontró el favorito para eliminar.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar favorito: " + e.getMessage());
        }
    }


}
