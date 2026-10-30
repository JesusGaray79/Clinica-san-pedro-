package Datos;

import Modelo.Paciente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PacienteDAO implements Crud<Paciente> {

    private Paciente mapear(ResultSet rs) throws SQLException {
        return new Paciente(rs.getString("dni"), rs.getString("nombres"), rs.getInt("edad"),
                rs.getString("telefono"), rs.getString("sexo"));
    }

    @Override
    public void registrar(Paciente p) throws SQLException {
        String sql = "INSERT INTO paciente (dni, nombres, edad, telefono, sexo) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getDni());
            ps.setString(2, p.getNombres());
            ps.setInt(3, p.getEdad());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getSexo());
            ps.executeUpdate();
        }
    }

    public void modificar(Paciente p) throws SQLException {
        String sql = "UPDATE paciente SET nombres = ?, edad = ?, telefono = ?, sexo = ? WHERE dni = ?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombres());
            ps.setInt(2, p.getEdad());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getSexo());
            ps.setString(5, p.getDni());
            ps.executeUpdate();
        }
    }

    // Sobrecarga (Semana 3): listar() trae todos y listar(filtro) busca por DNI o nombre
    @Override
    public ArrayList<Paciente> listar() throws SQLException {
        return listar("");
    }

    public ArrayList<Paciente> listar(String filtro) throws SQLException {
        ArrayList<Paciente> lista = new ArrayList<>();
        String sql = "SELECT dni, nombres, edad, telefono, sexo FROM paciente "
                + "WHERE dni LIKE ? OR nombres LIKE ? ORDER BY nombres";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro + "%");
            ps.setString(2, "%" + filtro + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public Paciente buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT dni, nombres, edad, telefono, sexo FROM paciente WHERE dni = ?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }
}
