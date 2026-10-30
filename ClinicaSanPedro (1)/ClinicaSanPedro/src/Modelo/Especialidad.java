package Modelo;

public class Especialidad {

    private int id;
    private String nombre;

    public Especialidad() { }

    public Especialidad(int id, String nombre) {
        this.id = id;
        setNombre(nombre);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.trim().length() < 3 || nombre.trim().length() > 40
                || !nombre.trim().matches("[\\p{L} ]+")) {
            throw new IllegalArgumentException("La especialidad debe tener entre 3 y 40 letras.");
        }
        this.nombre = nombre.trim();
    }

    // Se redefinen métodos de la clase Object (Recurso de la Semana 9)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Especialidad)) return false;
        return id == ((Especialidad) obj).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
