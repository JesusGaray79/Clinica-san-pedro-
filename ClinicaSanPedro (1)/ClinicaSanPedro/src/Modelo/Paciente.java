package Modelo;

public class Paciente extends Persona implements Reportable {

    private int edad;
    private String sexo;   // "M" o "F"

    public Paciente() { }

    public Paciente(String dni, String nombres, int edad, String telefono, String sexo) {
        super(dni, nombres, telefono);   // constructor del padre
        setEdad(edad);
        setSexo(sexo);
    }

    public int getEdad() { return edad; }
    public String getSexo() { return sexo; }

    public String getSexoTexto() {
        return "M".equals(sexo) ? "Masculino" : "Femenino";
    }

    public final void setEdad(int edad) {
        if (edad < 0 || edad > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años.");
        }
        this.edad = edad;
    }

    public final void setSexo(String sexo) {
        if (!"M".equals(sexo) && !"F".equals(sexo)) {
            throw new IllegalArgumentException("El sexo debe ser M o F.");
        }
        this.sexo = sexo;
    }

    @Override
    public String descripcion() {
        return "Paciente: " + nombres + " (DNI " + dni + ", " + edad + " años)";
    }

    @Override
    public Object[] registrarDatos() {
        return new Object[]{dni, nombres, edad, telefono, getSexoTexto()};
    }
}
