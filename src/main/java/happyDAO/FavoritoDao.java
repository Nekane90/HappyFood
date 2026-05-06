package happyDAO;

import com.example.happyfood.conexion.ConexionDB;
import happyDTO.RecetaDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FavoritoDao {

        public boolean guardarFavorito(int idUsuario, int idReceta) {
            String sql = "INSERT INTO favoritos (id_usuario, id_receta) VALUES (?, ?) ON CONFLICT DO NOTHING";

            try (Connection con = ConexionDB.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);
                ps.setInt(2, idReceta);

                int filasAfecadas = ps.executeUpdate();
                return filasAfecadas > 0; // Si es 0, es que ya existía (gracias al ON CONFLICT)

            } catch (SQLException e) {
                System.err.println("Error al guardar favorito: " + e.getMessage());
                return false;
            }
        }

        public void eliminarFavorito(int idUsuario, int idReceta) {
            String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_receta = ?";

            try (Connection con = ConexionDB.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);
                ps.setInt(2, idReceta);
                ps.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Error al eliminar favorito: " + e.getMessage());
            }
        }

    // Comprueba si una receta ya es favorita para este usuario (para pintar el corazón rojo).

    public boolean esFavorito(int idUsuario, int idApi) {
        //Aquí unimos la tabla favoritos con recetas para buscar por el ID de la API
        String sql = "SELECT 1 FROM favoritos f " +
                "JOIN recetas r ON f.id_receta = r.id " +
                "WHERE f.id_usuario = ? AND r.id_api = ? LIMIT 1";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idApi);

            ResultSet rs = ps.executeQuery();
            return rs.next(); // Si hay un resultado, es que ya es favorito

        } catch (SQLException e) {
            System.err.println("❌ Error al comprobar favorito: " + e.getMessage());
            return false;
        }
    }
    //set es como una List, pero no repite los mismos ids
    public Set<Integer> obtenerIdsFavoritos(int idUsuario) {
        Set<Integer> favoritos = new HashSet<>();
        String sql = "SELECT r.id_api FROM favoritos f JOIN recetas r ON f.id_receta = r.id WHERE f.id_usuario = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                favoritos.add(rs.getInt("id_api"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return favoritos;
    }

    public List<RecetaDto> obtenerRecetasFavoritas(int idUsuario) {
        List<RecetaDto> lista = new ArrayList<>();
        // Ajusta los nombres de las columnas según tu tabla 'recetas'
        String sql = "SELECT r.id, r.titulo, r.instrucciones, r.tiempo_preparacion, " +
                "r.dificultad, r.imagen_url, r.id_api " +
                "FROM recetas r " +
                "JOIN favoritos f ON r.id = f.id_receta " +
                "WHERE f.id_usuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RecetaDto receta = new RecetaDto();
                receta.setId(rs.getInt("id"));
                receta.setTitulo(rs.getString("titulo"));
                receta.setInstrucciones(rs.getString("instrucciones"));
                receta.setTiempoPreparacion(rs.getInt("tiempo_preparacion"));
                receta.setDificultad(rs.getString("dificultad"));
                receta.setUrlImagen(rs.getString("imagen_url"));
                receta.setIdApi(rs.getInt("id_api"));

                lista.add(receta);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener recetas favoritas: " + e.getMessage());
        }
        return lista;
    }

}

