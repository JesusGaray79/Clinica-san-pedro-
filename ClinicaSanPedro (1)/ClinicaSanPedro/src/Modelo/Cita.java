package Modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Una cita se relaciona (asociación) con un Paciente, un Medico y el Usuario que la registró.
 * Implementa dos interfaces: Reportable y Cobrable (herencia múltiple con interfaces, Semana 9).
 */
public class Cita implements Reportable, Cobrable {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ATENDIDA = "ATENDIDA";
    public static final String CANCELADA = "CANCELADA";

    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public static final String[] COLUMNAS = {"Código", "Fecha", "Hora", "DNI", "Paciente",
        "Médico", "Especialidad", "Estado", "Monto (S/)"};

    private int id;
    private Paciente paciente;
    private Medico medico;
    private Usuario registradoPor;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String estado;
    private double monto;

    public Cita() { }

    // Constructor para una cita NUEVA: valida que no sea en el pasado
    public Cita(Paciente paciente, Medico medico, Usuario registradoPor,
                LocalDate fecha, LocalTime hora, String motivo) {
        if (paciente == null) throw new IllegalArgumentException("Busca y selecciona un paciente registrado.");
        if (medico == null) throw new IllegalArgumentException("Selecciona un médico.");
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de la cita no puede ser anterior a hoy.");
        }
        if (hora == null) throw new IllegalArgumentException("Selecciona un horario disponible.");
        this.paciente = paciente;
        this.medico = medico;
        this.registradoPor = registradoPor;
        this.fecha = fecha;
        this.hora = hora;
        setMotivo(motivo);
        this.estado = PENDIENTE;
        this.monto = calcularMonto();
    }

    // Constructor sobrecargado para una cita que ya existe en la base de datos
    public Cita(int id, Paciente paciente, Medico medico, Usuario registradoPor,
                LocalDate fecha, LocalTime hora, String motivo, String estado, double monto) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.registradoPor = registradoPor;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
        this.monto = monto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public Usuario getRegistradoPor() { return registradoPor; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getMotivo() { return motivo; }
    public String getEstado() { return estado; }
    public double getMonto() { return monto; }

    public final void setMotivo(String motivo) {
        if (motivo == null || motivo.trim().length() < 3) {
            throw new IllegalArgumentException("Ingresa el motivo de la consulta (mínimo 3 caracteres).");
        }
        if (motivo.trim().length() > 200) {
            throw new IllegalArgumentException("El motivo no puede superar los 200 caracteres.");
        }
        this.motivo = motivo.trim();
    }

    public String getCodigo() {
        return String.format("CIT%05d", id);
    }

    // Cambios de estado controlados por la propia clase (encapsulamiento)
    public void marcarAtendida() {
        if (!PENDIENTE.equals(estado)) {
            throw new IllegalStateException("Solo se puede atender una cita pendiente.");
        }
        estado = ATENDIDA;
    }

    public void cancelar() {
        if (!PENDIENTE.equals(estado)) {
            throw new IllegalStateException("Solo se puede cancelar una cita pendiente.");
        }
        estado = CANCELADA;
    }

    @Override
    public double calcularMonto() {
        return medico.getTarifa();
    }

    @Override
    public Object[] registrarDatos() {
        return new Object[]{getCodigo(), fecha.format(FORMATO_FECHA), hora.format(FORMATO_HORA),
            paciente.getDni(), paciente.getNombres(), medico.getNombres(),
            medico.getEspecialidad(), estado, String.format("%.2f", monto)};
    }
}
