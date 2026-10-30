package Vista;

import Datos.CitaDAO;
import Modelo.Cita;
import Modelo.Periodo;
import Modelo.Resumen;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 * Reportes del administrador: ganancia de hoy, de la semana y del mes (se actualiza sola)
 * y reporte de citas atendidas y canceladas por periodo diario, semanal, mensual o anual.
 */
public class FrmReportes extends javax.swing.JInternalFrame {

    private final CitaDAO dao = new CitaDAO();
    private ArrayList<Cita> detalle = new ArrayList<>();
    private final Timer temporizador;

    public FrmReportes() {
        initComponents();
        Util.configurarFecha(spnFecha);
        Util.configurarTabla(tblReporte, Cita.COLUMNAS);

        // actualización automática cada 30 segundos
        temporizador = new Timer(30000, e -> actualizarTarjetas());
        temporizador.start();

        actualizarTarjetas();
        generar();
        Util.mensaje(lblMensaje, "Las ganancias de arriba se actualizan solas cada 30 segundos.", false);
    }

    @Override
    public void dispose() {
        temporizador.stop();      // se detiene al cerrar el formulario
        super.dispose();
    }

    private void actualizarTarjetas() {
        try {
            LocalDate hoy = LocalDate.now();
            Periodo semana = new Periodo(Periodo.SEMANAL, hoy);
            Periodo mes = new Periodo(Periodo.MENSUAL, hoy);
            pintarTarjeta(dao.resumen(hoy, hoy), lblGananciaHoy, lblDetalleHoy);
            pintarTarjeta(dao.resumen(semana.getDesde(), semana.getHasta()), lblGananciaSemana, lblDetalleSemana);
            pintarTarjeta(dao.resumen(mes.getDesde(), mes.getHasta()), lblGananciaMes, lblDetalleMes);
            lblActualizado.setText("Última actualización: " + LocalTime.now().withNano(0));
        } catch (SQLException e) {
            lblActualizado.setText("No se pudo actualizar: " + e.getMessage());
        }
    }

    private void pintarTarjeta(Resumen r, javax.swing.JLabel valor, javax.swing.JLabel detalle) {
        valor.setText(String.format("S/ %.2f", r.getGanancia()));
        detalle.setText(r.getAtendidas() + " atendidas - " + r.getCanceladas() + " canceladas");
    }

    private void generar() {
        try {
            Periodo periodo = new Periodo((String) cbxPeriodo.getSelectedItem(), Util.fechaDe(spnFecha));
            Resumen r = dao.resumen(periodo.getDesde(), periodo.getHasta());
            detalle = dao.listar(periodo.getDesde(), periodo.getHasta());
            lblResultado.setText("Periodo " + periodo.getTipo().toLowerCase() + " (" + periodo + "):   "
                    + r.getAtendidas() + " atendidas  |  " + r.getCanceladas() + " canceladas  |  "
                    + r.getPendientes() + " pendientes  |  Total: " + r.getTotal()
                    + "  |  Ganancia: S/ " + String.format("%.2f", r.getGanancia()));
            mostrarDetalle();
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    // Filtra en memoria la lista del periodo según el estado elegido
    private void mostrarDetalle() {
        String estado = (String) cbxEstado.getSelectedItem();
        ArrayList<Cita> filtradas = new ArrayList<>();
        for (Cita c : detalle) {
            if ("Todos".equals(estado) || c.getEstado().equals(estado)) {
                filtradas.add(c);
            }
        }
        Util.llenarTabla(tblReporte, filtradas);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblGananciaHoy = new javax.swing.JLabel();
        lblDetalleHoy = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblGananciaSemana = new javax.swing.JLabel();
        lblDetalleSemana = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblGananciaMes = new javax.swing.JLabel();
        lblDetalleMes = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JButton();
        lblActualizado = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblPeriodo = new javax.swing.JLabel();
        cbxPeriodo = new javax.swing.JComboBox<>();
        lblFecha = new javax.swing.JLabel();
        spnFecha = new javax.swing.JSpinner();
        lblEstado = new javax.swing.JLabel();
        cbxEstado = new javax.swing.JComboBox<>();
        btnGenerar = new javax.swing.JButton();
        lblResultado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();
        lblMensaje = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(false);
        setTitle("Reportes de citas y ganancias");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("REPORTES DE CITAS Y GANANCIAS");

        getContentPane().add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 920, 30));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "GANANCIAS (suma de las citas atendidas)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "HOY", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGananciaHoy.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblGananciaHoy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGananciaHoy.setText("S/ 0.00");

        jPanel2.add(lblGananciaHoy, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 18, 270, 32));

        lblDetalleHoy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDetalleHoy.setText(" ");

        jPanel2.add(lblDetalleHoy, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 52, 270, 20));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 25, 290, 80));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ESTA SEMANA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGananciaSemana.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblGananciaSemana.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGananciaSemana.setText("S/ 0.00");

        jPanel3.add(lblGananciaSemana, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 18, 270, 32));

        lblDetalleSemana.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDetalleSemana.setText(" ");

        jPanel3.add(lblDetalleSemana, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 52, 270, 20));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 25, 290, 80));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ESTE MES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGananciaMes.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblGananciaMes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGananciaMes.setText("S/ 0.00");

        jPanel4.add(lblGananciaMes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 18, 270, 32));

        lblDetalleMes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDetalleMes.setText(" ");

        jPanel4.add(lblDetalleMes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 52, 270, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 25, 290, 80));

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setText("ACTUALIZAR AHORA");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        jPanel1.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 112, 170, 28));

        lblActualizado.setText(" ");

        jPanel1.add(lblActualizado, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 116, 500, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 920, 150));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REPORTE POR PERIODO", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPeriodo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPeriodo.setText("Periodo:");

        jPanel5.add(lblPeriodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 22, 150, 20));

        cbxPeriodo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Diario", "Semanal", "Mensual", "Anual" }));

        jPanel5.add(cbxPeriodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 44, 150, 28));

        lblFecha.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFecha.setText("Fecha de referencia:");

        jPanel5.add(lblFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 22, 150, 20));


        jPanel5.add(spnFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 44, 150, 28));

        lblEstado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEstado.setText("Estado:");

        jPanel5.add(lblEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 22, 160, 20));

        cbxEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "ATENDIDA", "CANCELADA", "PENDIENTE" }));
        cbxEstado.addActionListener(this::cbxEstadoActionPerformed);

        jPanel5.add(cbxEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 44, 160, 28));

        btnGenerar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGenerar.setText("GENERAR REPORTE");
        btnGenerar.addActionListener(this::btnGenerarActionPerformed);

        jPanel5.add(btnGenerar, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 42, 170, 33));

        lblResultado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblResultado.setText(" ");

        jPanel5.add(lblResultado, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 900, 25));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 920, 125));


        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Fecha", "Hora", "DNI", "Paciente", "Médico", "Especialidad", "Estado", "Monto (S/)"
            }
        ));
        jScrollPane1.setViewportView(tblReporte);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 920, 230));

        lblMensaje.setText(" ");

        getContentPane().add(lblMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 565, 920, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarTarjetas();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void cbxEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxEstadoActionPerformed
        mostrarDetalle();
    }//GEN-LAST:event_cbxEstadoActionPerformed

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        generar();
    }//GEN-LAST:event_btnGenerarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JComboBox<String> cbxEstado;
    private javax.swing.JComboBox<String> cbxPeriodo;
    private javax.swing.JLabel lblActualizado;
    private javax.swing.JLabel lblDetalleHoy;
    private javax.swing.JLabel lblDetalleMes;
    private javax.swing.JLabel lblDetalleSemana;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblGananciaHoy;
    private javax.swing.JLabel lblGananciaMes;
    private javax.swing.JLabel lblGananciaSemana;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblPeriodo;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spnFecha;
    private javax.swing.JTable tblReporte;
    // End of variables declaration//GEN-END:variables
}
