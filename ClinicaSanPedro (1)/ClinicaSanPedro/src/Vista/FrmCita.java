package Vista;

import Datos.CitaDAO;
import Datos.CruceHorarioException;
import Datos.EspecialidadDAO;
import Datos.MedicoDAO;
import Datos.PacienteDAO;
import Modelo.Cita;
import Modelo.Especialidad;
import Modelo.Medico;
import Modelo.Paciente;
import Modelo.Persona;
import Modelo.Usuario;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Formulario central: busca al paciente por DNI, elige especialidad, médico, fecha y hora,
 * y muestra en una tabla las citas registradas.
 */
public class FrmCita extends javax.swing.JInternalFrame {

    private final CitaDAO citaDAO = new CitaDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    private final Usuario sesion;
    private Paciente pacienteActual;                 // asociación: la cita se arma con estos objetos
    private ArrayList<Cita> lista = new ArrayList<>();
    private boolean cargando = true;                 // evita eventos mientras se llenan los combos

    public FrmCita(Usuario sesion) {
        this.sesion = sesion;
        initComponents();
        Util.configurarFecha(spnFecha);
        Util.configurarTabla(tblCita, Cita.COLUMNAS);
        cargarEspecialidades();
        cargarTabla();
        cargando = false;
    }

    // ------------------------- combos dependientes -------------------------
    private void cargarEspecialidades() {
        boolean previo = cargando;
        cargando = true;
        try {
            cbxEspecialidad.removeAllItems();
            for (Especialidad esp : especialidadDAO.listar()) {
                cbxEspecialidad.addItem(esp);
            }
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        } finally {
            cargando = previo;
        }
        cargarMedicos();
    }

    private void cargarMedicos() {
        Especialidad esp = (Especialidad) cbxEspecialidad.getSelectedItem();
        boolean previo = cargando;
        cargando = true;
        try {
            cbxMedico.removeAllItems();
            if (esp != null) {
                for (Medico m : medicoDAO.listarActivosPorEspecialidad(esp.getId())) {
                    cbxMedico.addItem(m);
                }
            }
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        } finally {
            cargando = previo;
        }
        actualizarTarifa();
        cargarHoras();
    }

    private void actualizarTarifa() {
        Medico m = (Medico) cbxMedico.getSelectedItem();
        lblTarifa.setText(m == null ? "Tarifa: -" : String.format("Tarifa: S/ %.2f", m.getTarifa()));
    }

    // Horarios de 08:00 a 17:30 cada 30 minutos, sin los que el médico ya tiene ocupados
    private void cargarHoras() {
        cbxHora.removeAllItems();
        Medico m = (Medico) cbxMedico.getSelectedItem();
        if (m == null) {
            Util.mensaje(lblMensaje, "No hay médicos activos en esa especialidad.", true);
            return;
        }
        try {
            LocalDate fecha = Util.fechaDe(spnFecha);
            ArrayList<LocalTime> ocupadas = citaDAO.horasOcupadas(m.getId(), fecha);
            LocalTime ahora = LocalTime.now();
            for (LocalTime h = LocalTime.of(8, 0); h.isBefore(LocalTime.of(18, 0)); h = h.plusMinutes(30)) {
                boolean paso = fecha.equals(LocalDate.now()) && h.isBefore(ahora);
                if (!ocupadas.contains(h) && !paso) {
                    cbxHora.addItem(h.format(Cita.FORMATO_HORA));
                }
            }
            if (cbxHora.getItemCount() == 0) {
                Util.mensaje(lblMensaje, "El médico no tiene horarios disponibles ese día.", true);
            } else {
                Util.mensaje(lblMensaje, " ", false);
            }
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    // ------------------------- acciones -------------------------
    private void buscarPaciente() {
        String dni = txtDni.getText().trim();
        if (!dni.matches("\\d{" + Persona.DNI_LONGITUD + "}")) {
            Util.error(this, "Ingresa un DNI de " + Persona.DNI_LONGITUD + " dígitos.");
            return;
        }
        try {
            pacienteActual = pacienteDAO.buscarPorDni(dni);
            if (pacienteActual == null) {
                lblPaciente.setText("Paciente no registrado (regístralo primero en el módulo Pacientes)");
                Util.mensaje(lblMensaje, "No existe un paciente con ese DNI.", true);
            } else {
                lblPaciente.setText(pacienteActual.getNombres() + " - " + pacienteActual.getEdad() + " años");
                Util.mensaje(lblMensaje, "Paciente encontrado.", false);
            }
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void registrarCita() {
        try {
            Medico medico = (Medico) cbxMedico.getSelectedItem();
            String hora = (String) cbxHora.getSelectedItem();
            // La propia clase Cita valida los datos al construirse
            Cita cita = new Cita(pacienteActual, medico, sesion, Util.fechaDe(spnFecha),
                    hora == null ? null : LocalTime.parse(hora), txtMotivo.getText());
            citaDAO.registrar(cita);
            Util.aviso(this, "Cita registrada correctamente.\nCódigo: " + cita.getCodigo()
                    + "\nMonto: S/ " + String.format("%.2f", cita.getMonto()));
            limpiar();
            cargarTabla();
            cargarHoras();
        } catch (IllegalArgumentException e) {
            Util.error(this, e.getMessage());
        } catch (CruceHorarioException e) {          // primero la excepción propia (más específica)
            Util.error(this, e.getMessage());
            cargarHoras();
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void limpiar() {
        txtDni.setText("");
        pacienteActual = null;
        lblPaciente.setText("Sin paciente seleccionado");
        txtMotivo.setText("");
        Util.mensaje(lblMensaje, " ", false);
    }

    private void cargarTabla() {
        try {
            switch (cbxFiltro.getSelectedIndex()) {
                case 0:
                    lista = citaDAO.listar(LocalDate.now(), LocalDate.now());
                    break;
                case 1:
                    lista = new ArrayList<>();
                    for (Cita c : citaDAO.listar()) {
                        if (Cita.PENDIENTE.equals(c.getEstado())) {
                            lista.add(c);
                        }
                    }
                    break;
                default:
                    lista = citaDAO.listar();
            }
            Util.llenarTabla(tblCita, lista);
        } catch (SQLException e) {
            Util.errorSQL(this, e);
        }
    }

    private void cambiarEstado(boolean atender) {
        int fila = tblCita.getSelectedRow();
        if (fila < 0) {
            Util.error(this, "Selecciona una cita de la tabla.");
            return;
        }
        Cita cita = lista.get(fila);
        String pregunta = (atender ? "¿Marcar como atendida la cita " : "¿Cancelar la cita ") + cita.getCodigo() + "?";
        if (!Util.confirmar(this, pregunta)) {
            return;
        }
        try {
            if (atender) {
                cita.marcarAtendida();
            } else {
                cita.cancelar();
            }
            if (citaDAO.cambiarEstado(cita.getId(), cita.getEstado()) == 0) {
                Util.error(this, "La cita ya no está pendiente.");
            }
            cargarTabla();
            cargarHoras();      // una cita cancelada libera su horario
        } catch (IllegalStateException e) {
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
        btnBuscar = new javax.swing.JButton();
        lblPaciente = new javax.swing.JLabel();
        lblEspecialidad = new javax.swing.JLabel();
        cbxEspecialidad = new javax.swing.JComboBox<>();
        lblMedico = new javax.swing.JLabel();
        cbxMedico = new javax.swing.JComboBox<>();
        lblTarifa = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        spnFecha = new javax.swing.JSpinner();
        lblHora = new javax.swing.JLabel();
        cbxHora = new javax.swing.JComboBox<>();
        lblMotivo = new javax.swing.JLabel();
        txtMotivo = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblFiltro = new javax.swing.JLabel();
        cbxFiltro = new javax.swing.JComboBox<>();
        btnAtender = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCita = new javax.swing.JTable();
        lblMensaje = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(false);
        setResizable(false);
        setTitle("Registro y gestión de citas médicas");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("REGISTRO Y GESTIÓN DE CITAS MÉDICAS");

        getContentPane().add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 980, 30));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "NUEVA CITA", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDni.setText("DNI del paciente:");

        jPanel1.add(lblDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 27, 170, 20));

        txtDni.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDniKeyTyped(evt);
            }
        });
        txtDni.addActionListener(this::txtDniActionPerformed);

        jPanel1.add(txtDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 49, 170, 28));

        btnBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        jPanel1.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 48, 85, 30));

        lblPaciente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPaciente.setText("Sin paciente seleccionado");

        jPanel1.add(lblPaciente, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 52, 670, 25));

        lblEspecialidad.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEspecialidad.setText("Especialidad:");

        jPanel1.add(lblEspecialidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 87, 220, 20));

        cbxEspecialidad.addActionListener(this::cbxEspecialidadActionPerformed);

        jPanel1.add(cbxEspecialidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 109, 220, 28));

        lblMedico.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblMedico.setText("Médico:");

        jPanel1.add(lblMedico, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 87, 260, 20));

        cbxMedico.addActionListener(this::cbxMedicoActionPerformed);

        jPanel1.add(cbxMedico, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 109, 260, 28));

        lblTarifa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTarifa.setText("Tarifa: -");

        jPanel1.add(lblTarifa, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 111, 150, 25));

        lblFecha.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFecha.setText("Fecha:");

        jPanel1.add(lblFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 87, 130, 20));

        spnFecha.addChangeListener(this::spnFechaStateChanged);

        jPanel1.add(spnFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 109, 130, 28));

        lblHora.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblHora.setText("Hora disponible:");

        jPanel1.add(lblHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 87, 150, 20));


        jPanel1.add(cbxHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 109, 150, 28));

        lblMotivo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblMotivo.setText("Motivo de la consulta:");

        jPanel1.add(lblMotivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 147, 620, 20));


        jPanel1.add(txtMotivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 169, 620, 28));

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setText("REGISTRAR CITA");
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);

        jPanel1.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 170, 33));

        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        jPanel1.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 160, 140, 33));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 980, 210));

        lblFiltro.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFiltro.setText("Mostrar:");

        getContentPane().add(lblFiltro, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 262, 200, 20));

        cbxFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Citas de hoy", "Citas pendientes", "Todas las citas" }));
        cbxFiltro.addActionListener(this::cbxFiltroActionPerformed);

        getContentPane().add(cbxFiltro, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 284, 200, 28));

        btnAtender.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAtender.setText("MARCAR COMO ATENDIDA");
        btnAtender.addActionListener(this::btnAtenderActionPerformed);

        getContentPane().add(btnAtender, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 282, 210, 33));

        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelar.setText("CANCELAR CITA");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        getContentPane().add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 282, 160, 33));


        tblCita.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Fecha", "Hora", "DNI", "Paciente", "Médico", "Especialidad", "Estado", "Monto (S/)"
            }
        ));
        jScrollPane1.setViewportView(tblCita);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 980, 230));

        lblMensaje.setText(" ");

        getContentPane().add(lblMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 555, 980, 25));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDniKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniKeyTyped
        Util.soloNumeros(evt, txtDni, Persona.DNI_LONGITUD, lblMensaje, "DNI");
    }//GEN-LAST:event_txtDniKeyTyped

    private void txtDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDniActionPerformed
        buscarPaciente();
    }//GEN-LAST:event_txtDniActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarPaciente();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void cbxEspecialidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxEspecialidadActionPerformed
        if (!cargando) {
            cargarMedicos();
        }
    }//GEN-LAST:event_cbxEspecialidadActionPerformed

    private void cbxMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxMedicoActionPerformed
        if (!cargando) {
            actualizarTarifa();
            cargarHoras();
        }
    }//GEN-LAST:event_cbxMedicoActionPerformed

    private void spnFechaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnFechaStateChanged
        if (!cargando) {
            cargarHoras();
        }
    }//GEN-LAST:event_spnFechaStateChanged

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        registrarCita();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void cbxFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxFiltroActionPerformed
        if (!cargando) {
            cargarTabla();
        }
    }//GEN-LAST:event_cbxFiltroActionPerformed

    private void btnAtenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtenderActionPerformed
        cambiarEstado(true);
    }//GEN-LAST:event_btnAtenderActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cambiarEstado(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtender;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<Especialidad> cbxEspecialidad;
    private javax.swing.JComboBox<String> cbxFiltro;
    private javax.swing.JComboBox<String> cbxHora;
    private javax.swing.JComboBox<Medico> cbxMedico;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblEspecialidad;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFiltro;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblMedico;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblMotivo;
    private javax.swing.JLabel lblPaciente;
    private javax.swing.JLabel lblTarifa;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spnFecha;
    private javax.swing.JTable tblCita;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtMotivo;
    // End of variables declaration//GEN-END:variables
}
