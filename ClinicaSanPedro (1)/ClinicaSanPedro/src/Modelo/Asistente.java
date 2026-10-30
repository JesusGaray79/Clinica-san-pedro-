package Modelo;

/** El asistente (o enfermera) solo registra citas y pacientes. */
public class Asistente extends Usuario {

    public static final String ROL = "ASISTENTE";

    public Asistente() { }

    public Asistente(int id, String dni, String nombres, String telefono,
                     String username, String claveHash, boolean activo) {
        super(id, dni, nombres, telefono, username, claveHash, activo);
    }

    @Override
    public boolean puedeAcceder(String modulo) {
        return MOD_CITAS.equals(modulo) || MOD_PACIENTES.equals(modulo);
    }

    @Override
    public String getRol() {
        return ROL;
    }
}
