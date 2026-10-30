package Datos;

import java.sql.SQLException;
import java.util.ArrayList;

/** Interfaz que implementan todos los DAO: registrar y listar (Semana 9). */
public interface Crud<T> {

    void registrar(T objeto) throws SQLException;

    ArrayList<T> listar() throws SQLException;
}
