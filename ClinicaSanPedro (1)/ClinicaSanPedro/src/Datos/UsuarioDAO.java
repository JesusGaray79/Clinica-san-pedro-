package Datos;

import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioDAO implements Crud<Usuario> {

    private static final String COLUMNAS = "id, dni, nombres, telefono, username, clave, rol, activo";

    // Crea un Administrador o un Asistente según el rol guardado (polimorfismo)
    private Usuario mapear(ResultSet rs) throws SQLException {
        return Usuario.crear(rs.getString("rol"), rs.getInt("id"), rs.getString("dni"),
                rs.getString("nombres"), rs.getString("telefono"), rs.getString("username"),
                rs.getString("clave"), rs.getBoolean("activo"));
    }

    /** Devuelve el usuario si las credenciales son correctas y está activo; si no, null. */
    public Usuario autenticar(String username, String claveTextoPlano) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM usuario WHERE username = ? AND clave = ? AND activo = TRUE";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, Modelo.Seguridad.hash(claveTextoPlano));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public void registrar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (dni, nombres, telefono, username, clave, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getDni());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getUsername());
            ps.setString(5, u.getClave());
            ps.setString(6, u.getRol());
            ps.executeUpdate();
        }
    }

    public void modificar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET dni = ?, nombres = ?, telefono = ?, username = ?, clave = ?, rol = ? WHERE id = ?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getDni());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getUsername());
            ps.setString(5, u.getClave());
            ps.setString(6, u.getRol());
            ps.setInt(7, u.getId());
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE usuario SET activo = ? WHERE id = ?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public ArrayList<Usuario> listar() throws SQLException {
        ArrayList<Usuario> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNAS + " FROM usuario ORDER BY nombres");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }
}
