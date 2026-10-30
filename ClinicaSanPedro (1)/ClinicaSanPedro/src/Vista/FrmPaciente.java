package Vista;

import Datos.PacienteDAO;
import Modelo.Paciente;
import Modelo.Persona;
import java.sql.SQLException;
import java.util.ArrayList;

/** Registro, búsqueda y actualización de pacientes. */
public class FrmPaciente extends javax.swing.JInternalFrame {

    private final PacienteDAO dao = new PacienteDAO();
    private ArrayList<Paciente> lista = new ArrayList<>();

    public FrmPaciente() {
        initComponents();
        Util.configurarTabla(tblPaciente, new String[]{"DNI", "Nombres", "Edad", "Teléfono", "Sexo"});
        cargar();
    }

    private void cargar() {
        try {
            lista = dao.listar(txtBuscar.getText().trim());
            Util.llenarTabla(tblPaciente, lista);
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void mostrar(Paciente p) {
        txtDni.setText(p.getDni());
        txtDni.setEditable(false);          // el DNI identifica al paciente: no se modifica
        txtNombres.setText(p.getNombres());
        txtEdad.setText(String.valueOf(p.getEdad()));
        txtTelefono.setText(p.getTelefono());
        cbxSexo.setSelectedIndex("M".equals(p.getSexo()) ? 0 : 1);
        Util.mensaje(lblMensaje, "Paciente seleccionado: puedes actualizarlo o ver su historial.", false);
    }

    private void seleccionarFila() {
        int fila = tblPaciente.getSelectedRow();
        if (fila >= 0 && fila < lista.size()) {
            mostrar(lista.get(fila));
        }
    }

    private void limpiar() {
        txtDni.setText("");
        txtDni.setEditable(true);
        txtNombres.setText("");
        txtEdad.setText("");
        txtTelefono.setText("");
        cbxSexo.setSelectedIndex(0);
        tblPaciente.clearSelection();
        Util.mensaje(lblMensaje, " ", false);
        txtDni.requestFocus();
    }

    // Los datos se validan dentro de la clase Paciente; aquí solo se capturan sus errores
    private Paciente leerFormulario() {
        int edad;
        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La edad debe ser un número.");
        }
        String sexo = cbxSexo.getSelectedIndex() == 0 ? "M" : "F";
        return new Paciente(txtDni.getText().trim(), txtNombres.getText().trim(), edad,
                txtTelefono.getText().trim(), sexo);
    }

    private void registrar() {
        if (!txtDni.isEditable()) {
            Util.error(this, "Hay un paciente seleccionado. Presiona LIMPIAR para registrar uno nuevo.");
            return;
        }
        try {
            dao.registrar(leerFormulario());
            Util.aviso(this, "Paciente registrado correctamente.");
            limpiar();
            cargar();
        } catch (IllegalArgumentException e) {
            Util.error(this, e.getMessage());
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void actualizar() {
        if (txtDni.isEditable()) {
            Util.error(this, "Selecciona un paciente de la tabla para actualizarlo.");
            return;
        }
        try {
            dao.modificar(leerFormulario());
            Util.aviso(this, "Datos del paciente actualizados.");
            limpiar();
            cargar();
        } catch (IllegalArgumentException e) {
            Util.error(this, e.getMessage());
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void verHistorial() {
        int fila = tblPaciente.getSelectedRow();
        if (fila < 0) {
            Util.error(this, "Selecciona un paciente de la tabla.");
            return;
        }
        FrmHistorial frm = new FrmHistorial(lista.get(fila));
        getDesktopPane().add(frm);
        frm.setLocation(30, 30);
        frm.setVisible(true);
        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            // no pasa nada
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
        lblEdad = new javax.swing.JLabel();
        txtEdad = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblSexo = new javax.swing.JLabel();
        cbxSexo = new javax.swing.JComboBox<>();
        btnRegistrar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnHistorial = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPaciente = new javax.swing.JTable();
        lblMensaje = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(false);
        setTitle("Registro de pacientes");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("REGISTRO DE PACIENTES");

        getContentPane().add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 880, 30));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DATOS DEL PACIENTE", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDni.setText("DNI:");

        jPanel1.add(lblDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 27, 180, 20));

        txtDni.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDniKeyTyped(evt);
            }
        });

        jPanel1.add(txtDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 49, 180, 28));

        lblNombres.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNombres.setText("Nombres y apellidos:");

        jPanel1.add(lblNombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 27, 270, 20));


        jPanel1.add(txtNombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 49, 270, 28));

        lblEdad.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEdad.setText("Edad:");

        jPanel1.add(lblEdad, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 27, 90, 20));

        txtEdad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEdadKeyTyped(evt);
            }
        });

        jPanel1.add(txtEdad, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 49, 90, 28));

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTelefono.setText("Teléfono:");

        jPanel1.add(lblTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 27, 140, 20));

        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoKeyTyped(evt);
            }
        });

        jPanel1.add(txtTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 49, 140, 28));

        lblSexo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSexo.setText("Sexo:");

        jPanel1.add(lblSexo, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 27, 130, 20));

        cbxSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino" }));

        jPanel1.add(cbxSexo, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 49, 130, 28));

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setText("REGISTRAR NUEVO");
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);

        jPanel1.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 92, 170, 33));

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setText("ACTUALIZAR SELECCIONADO");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        jPanel1.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 92, 210, 33));

        btnHistorial.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHistorial.setText("VER HISTORIAL DE CITAS");
        btnHistorial.addActionListener(this::btnHistorialActionPerformed);

        jPanel1.add(btnHistorial, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 92, 200, 33));

        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        jPanel1.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 92, 110, 33));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 880, 140));

        lblBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblBuscar.setText("Buscar por DNI o nombre:");

        getContentPane().add(lblBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 192, 400, 20));

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        getContentPane().add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 214, 400, 28));


        tblPaciente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DNI", "Nombres", "Edad", "Teléfono", "Sexo"
            }
        ));
        tblPaciente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPacienteMouseClicked(evt);
            }
        });
        tblPaciente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblPacienteKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblPaciente);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 880, 250));

        lblMensaje.setText(" ");

        getContentPane().add(lblMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 505, 880, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDniKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniKeyTyped
        Util.soloNumeros(evt, txtDni, Persona.DNI_LONGITUD, lblMensaje, "DNI");
    }//GEN-LAST:event_txtDniKeyTyped

    private void txtEdadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEdadKeyTyped
        Util.soloNumeros(evt, txtEdad, 3, lblMensaje, "Edad");
    }//GEN-LAST:event_txtEdadKeyTyped

    private void txtTelefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoKeyTyped
        Util.soloNumeros(evt, txtTelefono, Persona.TELEFONO_LONGITUD, lblMensaje, "Teléfono");
    }//GEN-LAST:event_txtTelefonoKeyTyped

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        registrar();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizar();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistorialActionPerformed
        verHistorial();
    }//GEN-LAST:event_btnHistorialActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        cargar();
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void tblPacienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPacienteMouseClicked
        seleccionarFila();
    }//GEN-LAST:event_tblPacienteMouseClicked

    private void tblPacienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPacienteKeyReleased
        seleccionarFila();
    }//GEN-LAST:event_tblPacienteKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnHistorial;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cbxSexo;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblEdad;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblNombres;
    private javax.swing.JLabel lblSexo;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPaciente;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtEdad;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
