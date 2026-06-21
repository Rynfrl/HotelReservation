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
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Hotel Reservation System", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(10, 60, 120));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(0,60));
        panel.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(40,20,80,25);
        form.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(130,20,220,25);
        form.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(40,60,80,25);
        form.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130,60,220,25);
        form.add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(130,110,100,30);
        btnLogin.setBackground(new Color(10, 90, 160));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener((ActionEvent e) -> doLogin());
        form.add(btnLogin);

        JButton btnExit = new JButton("Exit");
        btnExit.setBounds(250,110,100,30);
        btnExit.addActionListener(e -> System.exit(0));
        form.add(btnExit);

        panel.add(form, BorderLayout.CENTER);
        add(panel);
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
