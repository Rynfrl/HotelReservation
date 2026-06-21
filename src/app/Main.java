package app;

import javax.swing.SwingUtilities;
import config.DBConnection;
import view.login.LoginForm;

public class Main {
    public static void main(String[] args) {
        // Initialize DB (ensure driver loaded)
        DBConnection.init();
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
