package Modelo;

/**
 * Clase abstracta base (Semana 8). De ella heredan Paciente, Medico y Usuario.
 * Atributos protected (Semana 2/7) y validación dentro de los setters (encapsulamiento).
 */
public abstract class Persona {

    public static final int DNI_LONGITUD = 8;
    public static final int TELEFONO_LONGITUD = 9;

    protected String dni;
    protected String nombres;
    protected String telefono = "";

    // Constructor vacío, como en los ejemplos de clase
    public Persona() { }

    // Constructor sobrecargado con todos los datos
    public Persona(String dni, String nombres, String telefono) {
        setDni(dni);
        setNombres(nombres);
        setTelefono(telefono);
    }

    public String getDni() { return dni; }
    public String getNombres() { return nombres; }
    public String getTelefono() { return telefono; }

    // Los setters son final (Semana 8) porque se invocan desde el constructor
    public final void setDni(String dni) {
        if (dni == null || !dni.matches("\\d{" + DNI_LONGITUD + "}")) {
            throw new IllegalArgumentException("El DNI debe tener " + DNI_LONGITUD + " dígitos numéricos.");
        }
        this.dni = dni;
    }

    public final void setNombres(String nombres) {
        if (nombres == null || nombres.trim().length() < 3) {
            throw new IllegalArgumentException("Ingresa los nombres (mínimo 3 caracteres).");
        }
        String limpio = nombres.trim();
        if (limpio.length() > 80 || !limpio.matches("[\\p{L} .'-]+")) {
            throw new IllegalArgumentException("Los nombres solo admiten letras y espacios (máximo 80).");
        }
        this.nombres = limpio;
    }

    public final void setTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            this.telefono = "";
            return;
        }
        if (!telefono.trim().matches("\\d{" + TELEFONO_LONGITUD + "}")) {
            throw new IllegalArgumentException("El teléfono debe tener " + TELEFONO_LONGITUD + " dígitos numéricos.");
        }
        this.telefono = telefono.trim();
    }

    // Método abstracto: cada clase hija lo implementa a su manera (polimorfismo)
    public abstract String descripcion();

    @Override
    public String toString() {
        return nombres;
    }
}
