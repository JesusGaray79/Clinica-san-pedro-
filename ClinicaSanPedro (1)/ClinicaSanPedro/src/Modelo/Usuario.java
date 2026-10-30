package Modelo;

/**
 * Clase abstracta: un usuario del sistema es una Persona con credenciales.
 * Cada rol (Administrador, Asistente) define qué módulos puede usar (polimorfismo).
 */
public abstract class Usuario extends Persona implements Reportable {

    public static final String MOD_CITAS = "CITAS";
    public static final String MOD_PACIENTES = "PACIENTES";
    public static final String MOD_MEDICOS = "MEDICOS";
    public static final String MOD_USUARIOS = "USUARIOS";
    public static final String MOD_REPORTES = "REPORTES";

    private int id;
    private String username;
    private String clave;          // guarda el hash, nunca el texto plano
    private boolean activo = true;

    public Usuario() { }

    public Usuario(int id, String dni, String nombres, String telefono,
                   String username, String claveHash, boolean activo) {
        super(dni, nombres, telefono);
        this.id = id;
        setUsername(username);
        this.clave = claveHash;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getClave() { return clave; }
    public void setClave(String claveHash) { this.clave = claveHash; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public final void setUsername(String username) {
        if (username == null || !username.matches("[A-Za-z0-9_.]{4,20}")) {
            throw new IllegalArgumentException("El usuario debe tener de 4 a 20 caracteres (letras, números, _ o .).");
        }
        this.username = username;
    }

    // Método static: valida y cifra una contraseña en texto plano
    public static String cifrarClave(String textoPlano) {
        if (textoPlano == null || textoPlano.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        return Seguridad.hash(textoPlano);
    }

    // Método fábrica: devuelve el tipo de usuario según el rol
    public static Usuario crear(String rol, int id, String dni, String nombres, String telefono,
                                String username, String claveHash, boolean activo) {
        if (Administrador.ROL.equals(rol)) {
            return new Administrador(id, dni, nombres, telefono, username, claveHash, activo);
        }
        if (Asistente.ROL.equals(rol)) {
            return new Asistente(id, dni, nombres, telefono, username, claveHash, activo);
        }
        throw new IllegalArgumentException("Rol desconocido: " + rol);
    }

    // Métodos abstractos que cada rol implementa
    public abstract boolean puedeAcceder(String modulo);

    public abstract String getRol();

    @Override
    public String descripcion() {
        return "Usuario: " + nombres + " (" + getRol() + ")";
    }

    @Override
    public Object[] registrarDatos() {
        return new Object[]{id, dni, nombres, username, getRol(), activo ? "Activo" : "Inactivo"};
    }
}
