package happyDAO;


import com.example.happyfood.conexion.ConexionDB;
import happyDTO.PlanificadorSemanalDto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlanificadorSemanalDao {
    private PlanificadorSemanalDto planSemanal = new PlanificadorSemanalDto();


    public boolean guardarPlan(int idUser, String nombreMenu, String json) {
        String sql = "INSERT INTO planificadorsemanal (id_usuario, nombre_menu, contenido_json,fecha) VALUES (?,?, ?,?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, idUser);
            pstmt.setString(2, nombreMenu);
            pstmt.setString(3, json);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now())); //aqui guardamos la fecha actual

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //para sacar los menus que tiene guardados el usuario
    public List<PlanificadorSemanalDto> obtenerPlanesPorUsuario(int idUser) {
        List<PlanificadorSemanalDto> lista = new ArrayList<>();
        // Ordenamos por fecha para que el más reciente salga primero
        String sql = "SELECT id, nombre_menu, contenido_json, TO_CHAR(fecha, 'DD/MM/YYYY HH24:MI') as fecha_formateada " +
                "FROM planificadorsemanal WHERE id_usuario = ? ORDER BY fecha DESC";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUser);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new PlanificadorSemanalDto(
                        rs.getInt("id"),
                        rs.getString("nombre_menu"),
                        rs.getString("contenido_json"),
                        rs.getString("fecha_formateada")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminarPlan(int idPlan) {
        String sql = "DELETE FROM planificadorsemanal WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPlan);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se eliminó algo

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
