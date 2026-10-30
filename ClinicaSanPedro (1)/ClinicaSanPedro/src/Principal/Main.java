package Principal;

import Vista.FrmLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Clase principal: aplica el tema Nimbus y abre el inicio de sesión. */
public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // si Nimbus no está disponible se usa el aspecto por defecto
        }
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
