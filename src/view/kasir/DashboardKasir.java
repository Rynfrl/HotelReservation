package view.kasir;

import utils.Session;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardKasir extends JFrame {

    public DashboardKasir() {
        setTitle("Dashboard Kasir - Hotel Reservation");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(10, 60, 120));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(null);

        JLabel title = new JLabel("KASIR", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setBounds(10,10,200,40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebar.add(title);

        JButton btnTamu = new JButton("Data Tamu");
        btnTamu.setBounds(10,80,200,40);
        btnTamu.addActionListener((ActionEvent e) -> openFormTamu());
        sidebar.add(btnTamu);

        JButton btnReservasi = new JButton("Reservasi");
        btnReservasi.setBounds(10,130,200,40);
        btnReservasi.addActionListener(e -> openFormReservasi());
        sidebar.add(btnReservasi);

        JButton btnCheckIn = new JButton("Check-In");
        btnCheckIn.setBounds(10,180,200,40);
        btnCheckIn.addActionListener(e -> openFormCheckIn());
        sidebar.add(btnCheckIn);

        JButton btnCheckOut = new JButton("Check-Out");
        btnCheckOut.setBounds(10,230,200,40);
        btnCheckOut.addActionListener(e -> openFormCheckOut());
        sidebar.add(btnCheckOut);

        JButton btnPembayaran = new JButton("Pembayaran");
        btnPembayaran.setBounds(10,280,200,40);
        btnPembayaran.addActionListener(e -> openFormPembayaran());
        sidebar.add(btnPembayaran);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(10,330,200,40);
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0,80));
        JLabel lblWelcome = new JLabel("Selamat datang, " + getUserDisplay(), SwingConstants.LEFT);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        header.add(lblWelcome, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BorderLayout());
        JLabel info = new JLabel("<html><h2>Kasir Panel</h2><p>Gunakan menu di kiri untuk mengelola tamu, reservasi, check-in, check-out, dan pembayaran.</p></html>");
        info.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        content.add(info, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private String getUserDisplay() {
        User u = Session.getUser();
        return u != null ? u.getUsername() + " (" + u.getRole() + ")" : "Guest";
    }

    private void openFormTamu() {
        FormTamu ft = new FormTamu(this);
        ft.setVisible(true);
    }

    private void openFormReservasi() {
        FormReservasi fr = new FormReservasi(this);
        fr.setVisible(true);
    }

    private void openFormCheckIn() {
        FormCheckIn fci = new FormCheckIn(this);
        fci.setVisible(true);
    }

    private void openFormCheckOut() {
        FormCheckOut fco = new FormCheckOut(this);
        fco.setVisible(true);
    }

    private void openFormPembayaran() {
        FormPembayaran fp = new FormPembayaran(this);
        fp.setVisible(true);
    }

    private void logout() {
        Session.clear();
        this.dispose();
        new view.login.LoginForm().setVisible(true);
    }
}
