package Vista;

import Datos.EspecialidadDAO;
import Datos.MedicoDAO;
import Modelo.Especialidad;
import Modelo.Medico;
import Modelo.Persona;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/** Registro de médicos y de sus especialidades (solo el administrador). */
public class FrmMedico extends javax.swing.JInternalFrame {

    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();
    private ArrayList<Medico> lista = new ArrayList<>();
    private Medico seleccionado;            // null = se está registrando uno nuevo

    public FrmMedico() {
        initComponents();
        Util.configurarTabla(tblMedico, new String[]{"ID", "DNI", "Nombres", "Teléfono",
            "Especialidad", "Tarifa (S/)", "Estado"});
        cargarEspecialidades();
        cargar();
    }

    private void cargarEspecialidades() {
        try {
            cbxEspecialidad.removeAllItems();
            for (Especialidad esp : especialidadDAO.listar()) {
                cbxEspecialidad.addItem(esp);
            }
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void cargar() {
        try {
            lista = medicoDAO.listar();
            Util.llenarTabla(tblMedico, lista);
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void mostrar(Medico m) {
        seleccionado = m;
        txtDni.setText(m.getDni());
        txtNombres.setText(m.getNombres());
        txtTelefono.setText(m.getTelefono());
        cbxEspecialidad.setSelectedItem(m.getEspecialidad());
        txtTarifa.setText(String.valueOf(m.getTarifa()));
        btnEstado.setText(m.isActivo() ? "DESACTIVAR MÉDICO" : "ACTIVAR MÉDICO");
        Util.mensaje(lblMensaje, "Médico seleccionado: puedes actualizar sus datos o cambiar su estado.", false);
    }

    private void seleccionarFila() {
        int fila = tblMedico.getSelectedRow();
        if (fila >= 0 && fila < lista.size()) {
            mostrar(lista.get(fila));
        }
    }

    private void limpiar() {
        seleccionado = null;
        txtDni.setText("");
        txtNombres.setText("");
        txtTelefono.setText("");
        txtTarifa.setText("");
        if (cbxEspecialidad.getItemCount() > 0) {
            cbxEspecialidad.setSelectedIndex(0);
        }
        btnEstado.setText("ACTIVAR / DESACTIVAR");
        tblMedico.clearSelection();
        Util.mensaje(lblMensaje, " ", false);
        txtDni.requestFocus();
    }

    private Medico leerFormulario(int id, boolean activo) {
        double tarifa;
        try {
            tarifa = Double.parseDouble(txtTarifa.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La tarifa debe ser un número (ejemplo: 50 o 50.50).");
        }
        return new Medico(id, txtDni.getText().trim(), txtNombres.getText().trim(),
                txtTelefono.getText().trim(), (Especialidad) cbxEspecialidad.getSelectedItem(), tarifa, activo);
    }

    private void registrar() {
        if (seleccionado != null) {
            Util.error(this, "Hay un médico seleccionado. Presiona LIMPIAR para registrar uno nuevo.");
            return;
        }
        try {
            medicoDAO.registrar(leerFormulario(0, true));
            Util.aviso(this, "Médico registrado correctamente.");
            limpiar();
            cargar();
        } catch (IllegalArgumentException e) {
            Util.error(this, e.getMessage());
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void actualizar() {
        if (seleccionado == null) {
            Util.error(this, "Selecciona un médico de la tabla para actualizarlo.");
            return;
        }
        try {
            medicoDAO.modificar(leerFormulario(seleccionado.getId(), seleccionado.isActivo()));
            Util.aviso(this, "Datos del médico actualizados.");
            limpiar();
            cargar();
        } catch (IllegalArgumentException e) {
            Util.error(this, e.getMessage());
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void cambiarEstado() {
        if (seleccionado == null) {
            Util.error(this, "Selecciona un médico de la tabla.");
            return;
        }
        boolean nuevoEstado = !seleccionado.isActivo();
        if (!Util.confirmar(this, (nuevoEstado ? "¿Activar" : "¿Desactivar") + " al médico "
                + seleccionado.getNombres() + "?")) {
            return;
        }
        try {
            medicoDAO.cambiarEstado(seleccionado.getId(), nuevoEstado);
            limpiar();
            cargar();
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void nuevaEspecialidad() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva especialidad:",
                "Nueva especialidad", JOptionPane.PLAIN_MESSAGE);
        if (nombre == null) {
            return;   // presionó Cancelar
        }
        try {
            especialidadDAO.registrar(new Especialidad(0, nombre));
            cargarEspecialidades();
            for (int i = 0; i < cbxEspecialidad.getItemCount(); i++) {
                if (cbxEspecialidad.getItemAt(i).getNombre().equalsIgnoreCase(nombre.trim())) {
                    cbxEspecialidad.setSelectedIndex(i);
                }
            }
            Util.mensaje(lblMensaje, "Especialidad registrada: " + nombre.trim(), false);
        } catch (IllegalArgumentException e) {
            Util.error(this, e.getMessage());
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblDni = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lblNombres = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblEspecialidad = new javax.swing.JLabel();
        cbxEspecialidad = new javax.swing.JComboBox<>();
        btnNuevaEsp = new javax.swing.JButton();
        lblTarifa = new javax.swing.JLabel();
        txtTarifa = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEstado = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedico = new javax.swing.JTable();
        lblMensaje = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(false);
        setTitle("Registro de médicos y especialidades");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("REGISTRO DE MÉDICOS Y ESPECIALIDADES");

        getContentPane().add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 880, 30));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DATOS DEL MÉDICO", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDni.setText("DNI:");

        jPanel1.add(lblDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 27, 150, 20));

        txtDni.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDniKeyTyped(evt);
            }
        });

        jPanel1.add(txtDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 49, 150, 28));

        lblNombres.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNombres.setText("Nombres y apellidos:");

        jPanel1.add(lblNombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 27, 300, 20));


        jPanel1.add(txtNombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 49, 300, 28));

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTelefono.setText("Teléfono:");

        jPanel1.add(lblTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 27, 130, 20));

        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoKeyTyped(evt);
            }
        });

        jPanel1.add(txtTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 49, 130, 28));

        lblEspecialidad.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEspecialidad.setText("Especialidad:");

        jPanel1.add(lblEspecialidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 27, 190, 20));


        jPanel1.add(cbxEspecialidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 49, 190, 28));

        btnNuevaEsp.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNuevaEsp.setText("+");
        btnNuevaEsp.setToolTipText("Registrar una nueva especialidad");
        btnNuevaEsp.addActionListener(this::btnNuevaEspActionPerformed);

        jPanel1.add(btnNuevaEsp, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 48, 45, 30));

        lblTarifa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTarifa.setText("Tarifa por cita (S/):");

        jPanel1.add(lblTarifa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 87, 160, 20));

        txtTarifa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTarifaKeyTyped(evt);
            }
        });

        jPanel1.add(txtTarifa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 109, 160, 28));

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setText("REGISTRAR NUEVO");
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);

        jPanel1.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 107, 160, 33));

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setText("ACTUALIZAR SELECCIONADO");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        jPanel1.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 107, 210, 33));

        btnEstado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEstado.setText("ACTIVAR / DESACTIVAR");
        btnEstado.addActionListener(this::btnEstadoActionPerformed);

        jPanel1.add(btnEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 107, 180, 33));

        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        jPanel1.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 107, 90, 33));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 880, 150));


        tblMedico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DNI", "Nombres", "Teléfono", "Especialidad", "Tarifa (S/)", "Estado"
            }
        ));
        tblMedico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMedicoMouseClicked(evt);
            }
        });
        tblMedico.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblMedicoKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblMedico);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 205, 880, 295));

        lblMensaje.setText(" ");

        getContentPane().add(lblMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 505, 880, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDniKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniKeyTyped
        Util.soloNumeros(evt, txtDni, Persona.DNI_LONGITUD, lblMensaje, "DNI");
    }//GEN-LAST:event_txtDniKeyTyped

    private void txtTelefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoKeyTyped
        Util.soloNumeros(evt, txtTelefono, Persona.TELEFONO_LONGITUD, lblMensaje, "Teléfono");
    }//GEN-LAST:event_txtTelefonoKeyTyped

    private void btnNuevaEspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaEspActionPerformed
        nuevaEspecialidad();
    }//GEN-LAST:event_btnNuevaEspActionPerformed

    private void txtTarifaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTarifaKeyTyped
        Util.soloDecimal(evt, txtTarifa, lblMensaje, "Tarifa");
    }//GEN-LAST:event_txtTarifaKeyTyped

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        registrar();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizar();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstadoActionPerformed
        cambiarEstado();
    }//GEN-LAST:event_btnEstadoActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void tblMedicoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMedicoMouseClicked
        seleccionarFila();
    }//GEN-LAST:event_tblMedicoMouseClicked

    private void tblMedicoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblMedicoKeyReleased
        seleccionarFila();
    }//GEN-LAST:event_tblMedicoKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEstado;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnNuevaEsp;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<Especialidad> cbxEspecialidad;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblEspecialidad;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblNombres;
    private javax.swing.JLabel lblTarifa;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMedico;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtTarifa;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
