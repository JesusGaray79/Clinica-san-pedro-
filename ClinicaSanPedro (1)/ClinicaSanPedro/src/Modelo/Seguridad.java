package Modelo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria (final + métodos static, Semanas 3 y 8).
 * Las contraseñas se guardan cifradas con SHA-256 para proteger los datos
 * de acuerdo con la Ley N° 29733 mencionada en el informe.
 */
public final class Seguridad {

    private Seguridad() { }

    public static String hash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible", e);
        }
    }
}
