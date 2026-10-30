package Modelo;

public class Medico extends Persona implements Reportable {

    private int id;
    private Especialidad especialidad;   // composición: un médico TIENE una especialidad
    private double tarifa;
    private boolean activo = true;

    public Medico() { }

    public Medico(int id, String dni, String nombres, String telefono,
                  Especialidad especialidad, double tarifa, boolean activo) {
        super(dni, nombres, telefono);
        this.id = id;
        setEspecialidad(especialidad);
        setTarifa(tarifa);
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Especialidad getEspecialidad() { return especialidad; }
    public double getTarifa() { return tarifa; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public final void setEspecialidad(Especialidad especialidad) {
        if (especialidad == null) {
            throw new IllegalArgumentException("Selecciona una especialidad.");
        }
        this.especialidad = especialidad;
    }

    public final void setTarifa(double tarifa) {
        if (tarifa <= 0 || tarifa > 1000) {
            throw new IllegalArgumentException("La tarifa debe ser mayor que 0 y no superar S/ 1000.");
        }
        this.tarifa = tarifa;
    }

    @Override
    public String descripcion() {
        return "Médico: " + nombres + " - " + especialidad + " (S/ " + String.format("%.2f", tarifa) + ")";
    }

    @Override
    public Object[] registrarDatos() {
        return new Object[]{id, dni, nombres, telefono, especialidad,
            String.format("%.2f", tarifa), activo ? "Activo" : "Inactivo"};
    }
}
