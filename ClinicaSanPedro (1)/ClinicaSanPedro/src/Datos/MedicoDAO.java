package Datos;

import Modelo.Especialidad;
import Modelo.Medico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MedicoDAO implements Crud<Medico> {

    private static final String SELECT_BASE =
            "SELECT m.id, m.dni, m.nombres, m.telefono, m.tarifa, m.activo, "
            + "e.id AS esp_id, e.nombre AS esp_nombre "
            + "FROM medico m JOIN especialidad e ON e.id = m.id_especialidad ";

    private Medico mapear(ResultSet rs) throws SQLException {
        Especialidad esp = new Especialidad(rs.getInt("esp_id"), rs.getString("esp_nombre"));
        return new Medico(rs.getInt("id"), rs.getString("dni"), rs.getString("nombres"),
                rs.getString("telefono"), esp, rs.getDouble("tarifa"), rs.getBoolean("activo"));
    }

    @Override
    public void registrar(Medico m) throws SQLException {
        String sql = "INSERT INTO medico (dni, nombres, telefono, id_especialidad, tarifa) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getDni());
            ps.setString(2, m.getNombres());
            ps.setString(3, m.getTelefono());
            ps.setInt(4, m.getEspecialidad().getId());
            ps.setDouble(5, m.getTarifa());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setId(keys.getInt(1));
                }
            }
        }
    }

    public void modificar(Medico m) throws SQLException {
        String sql = "UPDATE medico SET dni = ?, nombres = ?, telefono = ?, id_especialidad = ?, tarifa = ? WHERE id = ?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, m.getDni());
            ps.setString(2, m.getNombres());
            ps.setString(3, m.getTelefono());
            ps.setInt(4, m.getEspecialidad().getId());
            ps.setDouble(5, m.getTarifa());
            ps.setInt(6, m.getId());
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE medico SET activo = ? WHERE id = ?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public ArrayList<Medico> listar() throws SQLException {
        ArrayList<Medico> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(SELECT_BASE + "ORDER BY m.nombres");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // Solo médicos activos de una especialidad (para el formulario de citas)
    public ArrayList<Medico> listarActivosPorEspecialidad(int idEspecialidad) throws SQLException {
        ArrayList<Medico> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE m.activo = TRUE AND m.id_especialidad = ? ORDER BY m.nombres";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }
}
