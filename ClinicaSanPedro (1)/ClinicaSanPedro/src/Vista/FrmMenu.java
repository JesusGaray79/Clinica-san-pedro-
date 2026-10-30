package Vista;

import Modelo.Usuario;
import java.beans.PropertyVetoException;
import java.util.function.Supplier;
import javax.swing.JInternalFrame;

/**
 * Menú principal con escritorio de formularios internos.
 * Cada opción se muestra solo si el usuario puede acceder al módulo: se le pregunta al
 * objeto Usuario (polimorfismo), sin importar si es Administrador o Asistente.
 */
public class FrmMenu extends javax.swing.JFrame {

    private final Usuario usuario;

    public FrmMenu(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
        setTitle("Sistema de Citas Médicas - " + usuario.getNombres() + " (" + usuario.getRol() + ")");
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        aplicarPermisos();
    }

    private void aplicarPermisos() {
        mnuCitas.setVisible(usuario.puedeAcceder(Usuario.MOD_CITAS));
        mnuPacientes.setVisible(usuario.puedeAcceder(Usuario.MOD_PACIENTES));
        itmMedicos.setVisible(usuario.puedeAcceder(Usuario.MOD_MEDICOS));
        itmUsuarios.setVisible(usuario.puedeAcceder(Usuario.MOD_USUARIOS));
        mnuAdministracion.setVisible(itmMedicos.isVisible() || itmUsuarios.isVisible());
        mnuReportes.setVisible(usuario.puedeAcceder(Usuario.MOD_REPORTES));
    }

    // Abre el formulario interno; si ya estaba abierto, solo lo trae al frente
    private void abrir(Class<? extends JInternalFrame> clase, Supplier<? extends JInternalFrame> fabrica) {
        try {
            for (JInternalFrame f : dspEscritorio.getAllFrames()) {
                if (clase.isInstance(f)) {
                    f.setIcon(false);
                    f.setSelected(true);
                    f.toFront();
                    return;
                }
            }
            JInternalFrame frm = fabrica.get();
            dspEscritorio.add(frm);
            int x = Math.max(0, (dspEscritorio.getWidth() - frm.getWidth()) / 2);
            int y = Math.max(0, (dspEscritorio.getHeight() - frm.getHeight()) / 2);
            frm.setLocation(x, y);
            frm.setVisible(true);
            frm.setSelected(true);
        } catch (PropertyVetoException ex) {
            // el formulario simplemente no se selecciona
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dspEscritorio = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        itmCerrarSesion = new javax.swing.JMenuItem();
        itmSalir = new javax.swing.JMenuItem();
        mnuCitas = new javax.swing.JMenu();
        itmCitas = new javax.swing.JMenuItem();
        mnuPacientes = new javax.swing.JMenu();
        itmPacientes = new javax.swing.JMenuItem();
        mnuAdministracion = new javax.swing.JMenu();
        itmMedicos = new javax.swing.JMenuItem();
        itmUsuarios = new javax.swing.JMenuItem();
        mnuReportes = new javax.swing.JMenu();
        itmReportes = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Citas Médicas");

        javax.swing.GroupLayout dspEscritorioLayout = new javax.swing.GroupLayout(dspEscritorio);
        dspEscritorio.setLayout(dspEscritorioLayout);
        dspEscritorioLayout.setHorizontalGroup(
            dspEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1166, Short.MAX_VALUE)
        );
        dspEscritorioLayout.setVerticalGroup(
            dspEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 591, Short.MAX_VALUE)
        );

        mnuArchivo.setText("Archivo");

        itmCerrarSesion.setText("Cerrar sesión");
        itmCerrarSesion.addActionListener(this::itmCerrarSesionActionPerformed);
        mnuArchivo.add(itmCerrarSesion);

        itmSalir.setText("Salir");
        itmSalir.addActionListener(this::itmSalirActionPerformed);
        mnuArchivo.add(itmSalir);

        jMenuBar1.add(mnuArchivo);

        mnuCitas.setText("Citas");

        itmCitas.setText("Registrar y gestionar citas");
        itmCitas.addActionListener(this::itmCitasActionPerformed);
        mnuCitas.add(itmCitas);

        jMenuBar1.add(mnuCitas);

        mnuPacientes.setText("Pacientes");

        itmPacientes.setText("Registrar y consultar pacientes");
        itmPacientes.addActionListener(this::itmPacientesActionPerformed);
        mnuPacientes.add(itmPacientes);

        jMenuBar1.add(mnuPacientes);

        mnuAdministracion.setText("Administración");

        itmMedicos.setText("Médicos y especialidades");
        itmMedicos.addActionListener(this::itmMedicosActionPerformed);
        mnuAdministracion.add(itmMedicos);

        itmUsuarios.setText("Usuarios del sistema");
        itmUsuarios.addActionListener(this::itmUsuariosActionPerformed);
        mnuAdministracion.add(itmUsuarios);

        jMenuBar1.add(mnuAdministracion);

        mnuReportes.setText("Reportes");

        itmReportes.setText("Citas y ganancias");
        itmReportes.addActionListener(this::itmReportesActionPerformed);
        mnuReportes.add(itmReportes);

        jMenuBar1.add(mnuReportes);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dspEscritorio)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dspEscritorio)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itmCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmCerrarSesionActionPerformed
        new FrmLogin().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_itmCerrarSesionActionPerformed

    private void itmSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_itmSalirActionPerformed

    private void itmCitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmCitasActionPerformed
        abrir(FrmCita.class, () -> new FrmCita(usuario));
    }//GEN-LAST:event_itmCitasActionPerformed

    private void itmPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmPacientesActionPerformed
        abrir(FrmPaciente.class, FrmPaciente::new);
    }//GEN-LAST:event_itmPacientesActionPerformed

    private void itmMedicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmMedicosActionPerformed
        abrir(FrmMedico.class, FrmMedico::new);
    }//GEN-LAST:event_itmMedicosActionPerformed

    private void itmUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmUsuariosActionPerformed
        abrir(FrmUsuarios.class, () -> new FrmUsuarios(usuario));
    }//GEN-LAST:event_itmUsuariosActionPerformed

    private void itmReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmReportesActionPerformed
        abrir(FrmReportes.class, FrmReportes::new);
    }//GEN-LAST:event_itmReportesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane dspEscritorio;
    private javax.swing.JMenu mnuAdministracion;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenu mnuCitas;
    private javax.swing.JMenu mnuPacientes;
    private javax.swing.JMenu mnuReportes;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem itmCerrarSesion;
    private javax.swing.JMenuItem itmCitas;
    private javax.swing.JMenuItem itmMedicos;
    private javax.swing.JMenuItem itmPacientes;
    private javax.swing.JMenuItem itmReportes;
    private javax.swing.JMenuItem itmSalir;
    private javax.swing.JMenuItem itmUsuarios;
    // End of variables declaration//GEN-END:variables
}
