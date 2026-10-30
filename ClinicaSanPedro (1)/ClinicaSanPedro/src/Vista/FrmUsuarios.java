package Vista;

import Datos.UsuarioDAO;
import Modelo.Persona;
import Modelo.Usuario;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Módulo del administrador: crea usuarios y les asigna rol, usuario y contraseña.
 * El rol decide qué módulos verá la persona al iniciar sesión.
 */
public class FrmUsuarios extends javax.swing.JInternalFrame {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final Usuario sesion;           // usuario que inició sesión
    private ArrayList<Usuario> lista = new ArrayList<>();
    private Usuario seleccionado;

    public FrmUsuarios(Usuario sesion) {
        this.sesion = sesion;
        initComponents();
        Util.configurarTabla(tblUsuario, new String[]{"ID", "DNI", "Nombres", "Usuario", "Rol", "Estado"});
        cargar();
    }

    private void cargar() {
        try {
            lista = dao.listar();
            Util.llenarTabla(tblUsuario, lista);
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void mostrar(Usuario u) {
        seleccionado = u;
        txtDni.setText(u.getDni());
        txtNombres.setText(u.getNombres());
        txtTelefono.setText(u.getTelefono());
        txtUsuario.setText(u.getUsername());
        txtClave.setText("");
        cbxRol.setSelectedItem(u.getRol());
        btnEstado.setText(u.isActivo() ? "DESACTIVAR USUARIO" : "ACTIVAR USUARIO");
        Util.mensaje(lblMensaje, "Usuario seleccionado. Deja la contraseña vacía para conservar la actual.", false);
    }

    private void seleccionarFila() {
        int fila = tblUsuario.getSelectedRow();
        if (fila >= 0 && fila < lista.size()) {
            mostrar(lista.get(fila));
        }
    }

    private void limpiar() {
        seleccionado = null;
        txtDni.setText("");
        txtNombres.setText("");
        txtTelefono.setText("");
        txtUsuario.setText("");
        txtClave.setText("");
        cbxRol.setSelectedIndex(0);
        btnEstado.setText("ACTIVAR / DESACTIVAR");
        tblUsuario.clearSelection();
        Util.mensaje(lblMensaje, " ", false);
        txtDni.requestFocus();
    }

    private Usuario leerFormulario(int id, String claveHash, boolean activo) {
        return Usuario.crear((String) cbxRol.getSelectedItem(), id, txtDni.getText().trim(),
                txtNombres.getText().trim(), txtTelefono.getText().trim(),
                txtUsuario.getText().trim(), claveHash, activo);
    }

    private void registrar() {
        if (seleccionado != null) {
            Util.error(this, "Hay un usuario seleccionado. Presiona LIMPIAR para registrar uno nuevo.");
            return;
        }
        try {
            String hash = Usuario.cifrarClave(new String(txtClave.getPassword()));
            dao.registrar(leerFormulario(0, hash, true));
            Util.aviso(this, "Usuario registrado correctamente.");
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
            Util.error(this, "Selecciona un usuario de la tabla para actualizarlo.");
            return;
        }
        try {
            String plano = new String(txtClave.getPassword());
            String hash = plano.isEmpty() ? seleccionado.getClave() : Usuario.cifrarClave(plano);
            dao.modificar(leerFormulario(seleccionado.getId(), hash, seleccionado.isActivo()));
            Util.aviso(this, "Datos del usuario actualizados.");
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
            Util.error(this, "Selecciona un usuario de la tabla.");
            return;
        }
        if (seleccionado.getId() == sesion.getId()) {
            Util.error(this, "No puedes desactivar tu propio usuario.");
            return;
        }
        boolean nuevoEstado = !seleccionado.isActivo();
        if (!Util.confirmar(this, (nuevoEstado ? "¿Activar" : "¿Desactivar") + " al usuario "
                + seleccionado.getUsername() + "?")) {
            return;
        }
        try {
            dao.cambiarEstado(seleccionado.getId(), nuevoEstado);
            limpiar();
            cargar();
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
        lblRol = new javax.swing.JLabel();
        cbxRol = new javax.swing.JComboBox<>();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblClave = new javax.swing.JLabel();
        txtClave = new javax.swing.JPasswordField();
        btnRegistrar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEstado = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuario = new javax.swing.JTable();
        lblMensaje = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(false);
        setTitle("Usuarios del sistema");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("USUARIOS DEL SISTEMA");

        getContentPane().add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 880, 30));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DATOS DEL USUARIO", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
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

        lblRol.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblRol.setText("Rol:");

        jPanel1.add(lblRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 27, 190, 20));

        cbxRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ASISTENTE", "ADMINISTRADOR" }));

        jPanel1.add(cbxRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 49, 190, 28));

        lblUsuario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblUsuario.setText("Usuario:");

        jPanel1.add(lblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 87, 200, 20));


        jPanel1.add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 109, 200, 28));

        lblClave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblClave.setText("Contraseña (mín. 6 caracteres):");

        jPanel1.add(lblClave, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 87, 260, 20));


        jPanel1.add(txtClave, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 109, 260, 28));

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setText("REGISTRAR NUEVO");
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);

        jPanel1.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 170, 33));

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setText("ACTUALIZAR SELECCIONADO");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        jPanel1.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 150, 210, 33));

        btnEstado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEstado.setText("ACTIVAR / DESACTIVAR");
        btnEstado.addActionListener(this::btnEstadoActionPerformed);

        jPanel1.add(btnEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 150, 190, 33));

        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        jPanel1.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 150, 110, 33));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 880, 195));


        tblUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DNI", "Nombres", "Usuario", "Rol", "Estado"
            }
        ));
        tblUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuarioMouseClicked(evt);
            }
        });
        tblUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblUsuarioKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuario);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 880, 250));

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

    private void tblUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuarioMouseClicked
        seleccionarFila();
    }//GEN-LAST:event_tblUsuarioMouseClicked

    private void tblUsuarioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblUsuarioKeyReleased
        seleccionarFila();
    }//GEN-LAST:event_tblUsuarioKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEstado;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cbxRol;
    private javax.swing.JLabel lblClave;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblNombres;
    private javax.swing.JLabel lblRol;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtClave;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblUsuario;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
