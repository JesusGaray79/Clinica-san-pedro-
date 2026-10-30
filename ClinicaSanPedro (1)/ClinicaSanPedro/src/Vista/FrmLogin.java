package Vista;

import Datos.UsuarioDAO;
import Modelo.Usuario;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Inicio de sesión. Valida las credenciales en la base de datos y abre el menú
 * según el rol del usuario (Administrador o Asistente).
 */
public class FrmLogin extends javax.swing.JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public FrmLogin() {
        initComponents();
        getRootPane().setDefaultButton(btnIngresar);   // Enter = ingresar
        setLocationRelativeTo(null);                   // centra la ventana
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblClave = new javax.swing.JLabel();
        txtClave = new javax.swing.JPasswordField();
        btnIngresar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        lblPie = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Inicio de sesión");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("SISTEMA DE CITAS MÉDICAS");

        getContentPane().add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, 340, 30));

        lblSubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitulo.setText("Clínica San Pedro - Sede Los Olivos");

        getContentPane().add(lblSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 47, 340, 22));

        lblUsuario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblUsuario.setText("Usuario:");

        getContentPane().add(lblUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 85, 300, 20));


        getContentPane().add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 107, 300, 30));

        lblClave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblClave.setText("Contraseña:");

        getContentPane().add(lblClave, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 145, 300, 20));


        getContentPane().add(txtClave, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 167, 300, 30));

        btnIngresar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnIngresar.setText("INGRESAR");
        btnIngresar.addActionListener(this::btnIngresarActionPerformed);

        getContentPane().add(btnIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 140, 36));

        btnSalir.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSalir.setText("SALIR");
        btnSalir.addActionListener(this::btnSalirActionPerformed);

        getContentPane().add(btnSalir, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 220, 140, 36));

        lblPie.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPie.setText("Acceso solo para personal autorizado");

        getContentPane().add(lblPie, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 268, 340, 22));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
        ingresar();
    }//GEN-LAST:event_btnIngresarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnSalirActionPerformed

    private void ingresar() {
        String user = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());

        if (user.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa usuario y contraseña.");
            return;
        }
        try {
            Usuario usuario = usuarioDAO.autenticar(user, clave);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                txtClave.setText("");
                txtUsuario.requestFocus();
                return;
            }
            new FrmMenu(usuario).setVisible(true);
            this.dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con la base de datos.\n"
                    + "Revisa que MySQL esté encendido y la clave en Datos/Conexion.java.\n\n" + e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIngresar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel lblClave;
    private javax.swing.JLabel lblPie;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPasswordField txtClave;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
