package app;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import config.DBConnection;
import view.login.LoginForm;

public class Main {
    public static void main(String[] args) {
        // Setup FlatLaf
        try {
            FlatLightLaf.setup();
            // Custom UI properties for Deep Blue primary and Amber accent
            UIManager.put("Component.accentColor", Color.decode("#F59E0B")); // Amber
            UIManager.put("Button.background", Color.decode("#1E3A8A")); // Deep Blue
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.arc", 8); // Rounded corners
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Initialize DB (ensure driver loaded)
        DBConnection.init();
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
