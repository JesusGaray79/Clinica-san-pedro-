package Vista;

import Datos.CitaDAO;
import Modelo.Cita;
import Modelo.Paciente;
import java.sql.SQLException;
import java.util.ArrayList;

/** Historial de citas de un paciente (se abre desde el módulo de pacientes). */
public class FrmHistorial extends javax.swing.JInternalFrame {

    public FrmHistorial(Paciente paciente) {
        initComponents();
        Util.configurarTabla(tblHistorial, Cita.COLUMNAS);
        lblPaciente.setText(paciente.descripcion());
        try {
            ArrayList<Cita> citas = new CitaDAO().listarPorPaciente(paciente.getDni());
            Util.llenarTabla(tblHistorial, citas);
            Util.mensaje(lblMensaje, citas.isEmpty() ? "El paciente aún no tiene citas registradas."
                    : "Total de citas: " + citas.size(), false);
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPaciente = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHistorial = new javax.swing.JTable();
        lblMensaje = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(false);
        setTitle("Historial de citas");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPaciente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPaciente.setText("Paciente");

        getContentPane().add(lblPaciente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 900, 28));


        tblHistorial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Fecha", "Hora", "DNI", "Paciente", "Médico", "Especialidad", "Estado", "Monto (S/)"
            }
        ));
        jScrollPane1.setViewportView(tblHistorial);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 900, 250));

        lblMensaje.setText(" ");

        getContentPane().add(lblMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 900, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblPaciente;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblHistorial;
    // End of variables declaration//GEN-END:variables
}
