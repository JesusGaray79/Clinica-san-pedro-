package Datos;

import Modelo.Cita;
import Modelo.Especialidad;
import Modelo.Medico;
import Modelo.Paciente;
import Modelo.Resumen;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CitaDAO implements Crud<Cita> {

    private static final String SELECT_BASE =
            "SELECT c.id, c.fecha, c.hora, c.motivo, c.estado, c.monto, "
            + "p.dni AS p_dni, p.nombres AS p_nombres, p.edad AS p_edad, p.telefono AS p_telefono, p.sexo AS p_sexo, "
            + "m.id AS m_id, m.dni AS m_dni, m.nombres AS m_nombres, m.telefono AS m_telefono, "
            + "m.tarifa AS m_tarifa, m.activo AS m_activo, e.id AS e_id, e.nombre AS e_nombre, "
            + "u.id AS u_id, u.dni AS u_dni, u.nombres AS u_nombres, u.telefono AS u_telefono, "
            + "u.username AS u_username, u.clave AS u_clave, u.rol AS u_rol, u.activo AS u_activo "
            + "FROM cita c "
            + "JOIN paciente p ON p.dni = c.dni_paciente "
            + "JOIN medico m ON m.id = c.id_medico "
            + "JOIN especialidad e ON e.id = m.id_especialidad "
            + "JOIN usuario u ON u.id = c.id_usuario ";

    private static final String ORDEN = " ORDER BY c.fecha DESC, c.hora DESC";

    // Arma el objeto Cita con su Paciente, Medico (y su Especialidad) y Usuario
    private Cita mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente(rs.getString("p_dni"), rs.getString("p_nombres"),
                rs.getInt("p_edad"), rs.getString("p_telefono"), rs.getString("p_sexo"));
        Especialidad esp = new Especialidad(rs.getInt("e_id"), rs.getString("e_nombre"));
        Medico m = new Medico(rs.getInt("m_id"), rs.getString("m_dni"), rs.getString("m_nombres"),
                rs.getString("m_telefono"), esp, rs.getDouble("m_tarifa"), rs.getBoolean("m_activo"));
        Usuario u = Usuario.crear(rs.getString("u_rol"), rs.getInt("u_id"), rs.getString("u_dni"),
                rs.getString("u_nombres"), rs.getString("u_telefono"), rs.getString("u_username"),
                rs.getString("u_clave"), rs.getBoolean("u_activo"));
        return new Cita(rs.getInt("id"), p, m, u, rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime(), rs.getString("motivo"),
                rs.getString("estado"), rs.getDouble("monto"));
    }

    private ArrayList<Cita> leer(PreparedStatement ps) throws SQLException {
        ArrayList<Cita> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Registra la cita. Lanza CruceHorarioException si el médico ya tiene una cita a esa hora. */
    @Override
    public void registrar(Cita c) throws SQLException {
        if (existeCruce(c.getMedico().getId(), c.getFecha(), c.getHora())) {
            throw new CruceHorarioException("El médico ya tiene una cita el "
                    + c.getFecha().format(Cita.FORMATO_FECHA) + " a las " + c.getHora().format(Cita.FORMATO_HORA) + ".");
        }
        String sql = "INSERT INTO cita (dni_paciente, id_medico, id_usuario, fecha, hora, motivo, estado, monto) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getPaciente().getDni());
            ps.setInt(2, c.getMedico().getId());
            ps.setInt(3, c.getRegistradoPor().getId());
            ps.setDate(4, Date.valueOf(c.getFecha()));
            ps.setTime(5, Time.valueOf(c.getHora()));
            ps.setString(6, c.getMotivo());
            ps.setString(7, c.getEstado());
            ps.setDouble(8, c.getMonto());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getInt(1));
                }
            }
        }
    }

    public boolean existeCruce(int idMedico, LocalDate fecha, LocalTime hora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita WHERE id_medico = ? AND fecha = ? AND hora = ? AND estado <> 'CANCELADA'";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public ArrayList<LocalTime> horasOcupadas(int idMedico, LocalDate fecha) throws SQLException {
        ArrayList<LocalTime> horas = new ArrayList<>();
        String sql = "SELECT hora FROM cita WHERE id_medico = ? AND fecha = ? AND estado <> 'CANCELADA'";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horas.add(rs.getTime("hora").toLocalTime());
                }
            }
        }
        return horas;
    }

    // Sobrecarga: listar() trae todas; listar(desde, hasta) filtra por rango de fechas
    @Override
    public ArrayList<Cita> listar() throws SQLException {
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(SELECT_BASE + ORDEN)) {
            return leer(ps);
        }
    }

    public ArrayList<Cita> listar(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = SELECT_BASE + "WHERE c.fecha BETWEEN ? AND ?" + ORDEN;
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            return leer(ps);
        }
    }

    public ArrayList<Cita> listarPorPaciente(String dni) throws SQLException {
        String sql = SELECT_BASE + "WHERE c.dni_paciente = ?" + ORDEN;
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            return leer(ps);
        }
    }

    /** Solo cambia el estado si la cita sigue pendiente. Devuelve cuántas filas cambió. */
    public int cambiarEstado(int id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE cita SET estado = ? WHERE id = ? AND estado = 'PENDIENTE'";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate();
        }
    }

    /** Cuenta citas por estado y suma la ganancia (solo citas atendidas). */
    public Resumen resumen(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT estado, COUNT(*) AS n, COALESCE(SUM(monto), 0) AS total "
                + "FROM cita WHERE fecha BETWEEN ? AND ? GROUP BY estado";
        int atendidas = 0, canceladas = 0, pendientes = 0;
        double ganancia = 0;
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    switch (rs.getString("estado")) {
                        case Cita.ATENDIDA:
                            atendidas = rs.getInt("n");
                            ganancia = rs.getDouble("total");
                            break;
                        case Cita.CANCELADA:
                            canceladas = rs.getInt("n");
                            break;
                        default:
                            pendientes = rs.getInt("n");
                    }
                }
            }
        }
        return new Resumen(atendidas, canceladas, pendientes, ganancia);
    }
}
