package view.admin;

import dao.KamarDAO;
import dao.ReservasiDAO;
import service.PembayaranService;
import utils.Session;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardAdmin extends JFrame {
    private KamarDAO kamarDAO = new KamarDAO();
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private PembayaranService pembayaranService = new PembayaranService();

    private JLabel lblTotalKamar = new JLabel();
    private JLabel lblKamarTersedia = new JLabel();
    private JLabel lblKamarTerisi = new JLabel();
    private JLabel lblTotalReservasi = new JLabel();
    private JLabel lblTotalPendapatan = new JLabel();

    public DashboardAdmin() {
        setTitle("Dashboard Admin - Hotel Reservation");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadStats();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(10, 60, 120));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(null);

        JLabel title = new JLabel("ADMIN", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setBounds(10,10,200,40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebar.add(title);

        JButton btnKamar = new JButton("Data Kamar");
        btnKamar.setBounds(10,80,200,40);
        btnKamar.addActionListener((ActionEvent e) -> openFormKamar());
        sidebar.add(btnKamar);

        JButton btnUser = new JButton("Data User");
        btnUser.setBounds(10,130,200,40);
        btnUser.addActionListener((ActionEvent e) -> openFormUser());
        sidebar.add(btnUser);

        JButton btnReservasi = new JButton("Data Reservasi");
        btnReservasi.setBounds(10,180,200,40);
        btnReservasi.addActionListener(e -> openFormLaporan());
        sidebar.add(btnReservasi);

        JButton btnPembayaran = new JButton("Data Pembayaran");
        btnPembayaran.setBounds(10,230,200,40);
        btnPembayaran.addActionListener(e -> openFormPembayaranAdmin());
        sidebar.add(btnPembayaran);

        JButton btnLaporan = new JButton("Laporan");
        btnLaporan.setBounds(10,280,200,40);
        btnLaporan.addActionListener(e -> openFormLaporan());
        sidebar.add(btnLaporan);

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
        content.setLayout(null);

        JLabel statTitle = new JLabel("Statistik");
        statTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statTitle.setBounds(20,10,200,30);
        content.add(statTitle);

        lblTotalKamar.setBounds(20,50,300,30);
        content.add(lblTotalKamar);

        lblKamarTersedia.setBounds(20,90,300,30);
        content.add(lblKamarTersedia);

        lblKamarTerisi.setBounds(20,130,300,30);
        content.add(lblKamarTerisi);

        lblTotalReservasi.setBounds(20,170,300,30);
        content.add(lblTotalReservasi);

        lblTotalPendapatan.setBounds(20,210,400,30);
        content.add(lblTotalPendapatan);

        add(content, BorderLayout.CENTER);
    }

    private String getUserDisplay() {
        User u = Session.getUser();
        return u != null ? u.getUsername() + " (" + u.getRole() + ")" : "Guest";
    }

    private void loadStats() {
        int total = kamarDAO.countAll();
        int tersedia = kamarDAO.countByStatus("TERSEDIA");
        int terisi = kamarDAO.countByStatus("TERISI");
        int totalReservasi = reservasiDAO.countAll();
        double pendapatan = pembayaranService.totalPendapatan();

        lblTotalKamar.setText("Total kamar: " + total);
        lblKamarTersedia.setText("Kamar tersedia: " + tersedia);
        lblKamarTerisi.setText("Kamar terisi: " + terisi);
        lblTotalReservasi.setText("Total reservasi: " + totalReservasi);
        lblTotalPendapatan.setText("Total pendapatan: Rp " + String.format("%,.2f", pendapatan));
    }

    private void openFormKamar() {
        FormKamar fk = new FormKamar(this);
        fk.setVisible(true);
    }

    private void openFormUser() {
        FormUser fu = new FormUser(this);
        fu.setVisible(true);
    }

    private void openFormLaporan() {
        FormLaporan fl = new FormLaporan(this);
        fl.setVisible(true);
    }

    private void openFormPembayaranAdmin() {
        FormPembayaranAdmin fp = new FormPembayaranAdmin(this);
        fp.setVisible(true);
    }

    private void logout() {
        Session.clear();
        this.dispose();
        new view.login.LoginForm().setVisible(true);
    }
}
