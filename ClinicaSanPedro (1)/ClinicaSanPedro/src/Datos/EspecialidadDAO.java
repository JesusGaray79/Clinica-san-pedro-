package Datos;

import Modelo.Especialidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EspecialidadDAO implements Crud<Especialidad> {

    @Override
    public void registrar(Especialidad e) throws SQLException {
        String sql = "INSERT INTO especialidad (nombre) VALUES (?)";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.executeUpdate();
        }
    }

    @Override
    public ArrayList<Especialidad> listar() throws SQLException {
        ArrayList<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM especialidad ORDER BY nombre";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Especialidad(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return lista;
    }
}
