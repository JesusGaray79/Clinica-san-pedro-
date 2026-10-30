package Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase final con un método static que entrega la conexión a MySQL.
 * IMPORTANTE: cambia CLAVE por la contraseña de tu usuario root de MySQL.
 */
public final class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/clinica_san_pedro"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima";
    private static final String USUARIO = "root";
    private static final String CLAVE = "TU_CONTRASEÑA";

    private Conexion() { }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
