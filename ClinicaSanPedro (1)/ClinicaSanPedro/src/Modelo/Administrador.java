package Modelo;

/** El administrador maneja todo el sistema (indicación de la profesora). */
public class Administrador extends Usuario {

    public static final String ROL = "ADMINISTRADOR";

    public Administrador() { }

    public Administrador(int id, String dni, String nombres, String telefono,
                         String username, String claveHash, boolean activo) {
        super(id, dni, nombres, telefono, username, claveHash, activo);
    }

    @Override
    public boolean puedeAcceder(String modulo) {
        return true;
    }

    @Override
    public String getRol() {
        return ROL;
    }
}
