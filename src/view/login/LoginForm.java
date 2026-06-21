package view.login;

import service.LoginService;
import model.User;
import utils.Session;
import view.admin.DashboardAdmin;
import view.kasir.DashboardKasir;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private LoginService loginService = new LoginService();

    public LoginForm() {
        setTitle("Hotel Reservation - Login");
        setSize(800, 500); // Larger split-screen design
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel (Image / Branding area)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.decode("#1E3A8A")); // Deep Blue
        JLabel lblBranding = new JLabel("<html><div style='text-align: center; color: white;'>"
                + "<h2>Sistem Reservasi Hotel</h2>"
                + "<p>Manajemen Cepat, Tepat, dan Nyaman</p></div></html>", SwingConstants.CENTER);
        leftPanel.add(lblBranding, BorderLayout.CENTER);

        // Right Panel (Form area)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Selamat Datang");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.decode("#1F2937"));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Silakan masuk ke akun Anda");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 30, 20);
        rightPanel.add(lblSubtitle, gbc);

        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.gridwidth = 1;
        
        txtUsername = new JTextField(20);
        txtUsername.putClientProperty("JTextField.placeholderText", "Username");
        txtUsername.putClientProperty("JComponent.roundRect", true);
        gbc.gridy = 2;
        rightPanel.add(txtUsername, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.putClientProperty("JTextField.placeholderText", "Password");
        txtPassword.putClientProperty("JTextField.showRevealButton", true);
        txtPassword.putClientProperty("JComponent.roundRect", true);
        gbc.gridy = 3;
        rightPanel.add(txtPassword, gbc);

        JButton btnLogin = new JButton("<html><font color='white'>Login</font></html>");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        btnLogin.setBackground(Color.decode("#1E3A8A"));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener((ActionEvent e) -> doLogin());
        this.getRootPane().setDefaultButton(btnLogin); // Make Enter key work
        
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 20, 10, 20);
        rightPanel.add(btnLogin, gbc);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User u = loginService.authenticate(username, password);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Login gagal. Periksa username/password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Session.setUser(u);
        this.dispose();
        if ("ADMIN".equals(u.getRole())) {
            new DashboardAdmin().setVisible(true);
        } else {
            new DashboardKasir().setVisible(true);
        }
    }
}
