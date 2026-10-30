package Modelo;

/**
 * Interfaz para los objetos que se muestran como una fila de JTable.
 * Es el mismo método registrarDatos() que usamos en clase (FormEmpleado, FormCita).
 */
public interface Reportable {
    Object[] registrarDatos();
}
