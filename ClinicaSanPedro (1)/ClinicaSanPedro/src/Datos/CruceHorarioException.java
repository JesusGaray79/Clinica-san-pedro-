package Datos;

import java.sql.SQLException;

/** Excepción propia (Semana 3): el médico ya tiene una cita en esa fecha y hora. */
public class CruceHorarioException extends SQLException {

    public CruceHorarioException(String mensaje) {
        super(mensaje);
    }
}
