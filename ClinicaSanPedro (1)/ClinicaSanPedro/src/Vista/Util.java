package Vista;

import Modelo.Reportable;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Métodos de apoyo que comparten todos los formularios (mensajes, tablas y validación de teclas).
 * Clase final con métodos static (Semanas 3 y 8).
 */
public final class Util {

    private Util() { }

    // ----------------------------- mensajes -----------------------------
    /** Muestra un mensaje en la etiqueta del formulario (rojo = error, verde = correcto). */
    public static void mensaje(JLabel etiqueta, String texto, boolean esError) {
        etiqueta.setForeground(esError ? new Color(190, 30, 30) : new Color(30, 120, 50));
        etiqueta.setText(texto);
    }

    public static void aviso(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Dato no válido", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component padre, String texto) {
        return JOptionPane.showConfirmDialog(padre, texto, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void errorSQL(Component padre, SQLException e) {
        if (e.getErrorCode() == 1062) {
            error(padre, "Ya existe un registro con ese dato (DNI o usuario repetido).");
        } else {
            JOptionPane.showMessageDialog(padre, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----------------------------- tablas -----------------------------
    /** Deja la tabla lista: columnas, celdas no editables y una sola fila seleccionable. */
    public static void configurarTabla(JTable tabla, String[] columnas) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    /** Polimorfismo: llena la tabla con cualquier lista de objetos Reportable. */
    public static void llenarTabla(JTable tabla, ArrayList<? extends Reportable> lista) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (Reportable r : lista) {
            modelo.addRow(r.registrarDatos());
        }
        ajustarColumnas(tabla);
    }

    /** Ajusta el ancho de cada columna al contenido para que no se corten los textos. */
    public static void ajustarColumnas(JTable tabla) {
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        for (int c = 0; c < tabla.getColumnCount(); c++) {
            TableColumn columna = tabla.getColumnModel().getColumn(c);
            int ancho = tabla.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(tabla, columna.getHeaderValue(), false, false, 0, c)
                    .getPreferredSize().width;
            for (int f = 0; f < tabla.getRowCount(); f++) {
                ancho = Math.max(ancho, tabla.prepareRenderer(tabla.getCellRenderer(f, c), f, c)
                        .getPreferredSize().width);
            }
            columna.setPreferredWidth(Math.min(ancho + 18, 320));
        }
    }

    // ----------------------------- validación de teclas -----------------------------
    /** Para el evento keyTyped: solo deja escribir dígitos y avisa en rojo si se intenta otra tecla. */
    public static void soloNumeros(KeyEvent evt, JTextField campo, int maximo, JLabel mensaje, String nombre) {
        char c = evt.getKeyChar();
        if (Character.isISOControl(c)) {
            return;
        }
        if (!Character.isDigit(c)) {
            evt.consume();
            mensaje(mensaje, nombre + ": solo se permiten números.", true);
        } else if (campo.getText().length() >= maximo && campo.getSelectedText() == null) {
            evt.consume();
            mensaje(mensaje, nombre + ": máximo " + maximo + " dígitos.", true);
        } else {
            mensaje(mensaje, " ", false);
        }
    }

    /** Para el evento keyTyped: solo números y un punto decimal. */
    public static void soloDecimal(KeyEvent evt, JTextField campo, JLabel mensaje, String nombre) {
        char c = evt.getKeyChar();
        if (Character.isISOControl(c)) {
            return;
        }
        boolean puntoRepetido = (c == '.' && campo.getText().contains("."));
        if ((!Character.isDigit(c) && c != '.') || puntoRepetido) {
            evt.consume();
            mensaje(mensaje, nombre + ": solo números y un punto decimal.", true);
        } else {
            mensaje(mensaje, " ", false);
        }
    }

    // ----------------------------- fechas -----------------------------
    /** Convierte un JSpinner en selector de fecha con formato dd/MM/yyyy. */
    public static void configurarFecha(JSpinner spinner) {
        spinner.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
    }

    public static LocalDate fechaDe(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
