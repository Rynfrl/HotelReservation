package view.admin;

import dao.KamarDAO;
import dao.ReservasiDAO;
import service.PembayaranService;
import utils.Session;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardAdmin extends JFrame {
    private KamarDAO kamarDAO = new KamarDAO();
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private PembayaranService pembayaranService = new PembayaranService();

    private JLabel lblTotalKamar = new JLabel("0");
    private JLabel lblKamarTersedia = new JLabel("0");
    private JLabel lblKamarTerisi = new JLabel("0");
    private JLabel lblTotalReservasi = new JLabel("0");
    private JLabel lblTotalPendapatan = new JLabel("Rp 0");
    private BarChartPanel barChart;

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

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.decode("#1E3A8A")); // Deep Blue
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel title = new JLabel("<html><div style='text-align: center;'>Admin<br>Dashboard</div></html>", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        sidebar.add(createNavButton("Data Kamar", e -> openFormKamar()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Data User", e -> openFormUser()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Data Reservasi", e -> openFormLaporan()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Data Pembayaran", e -> openFormPembayaranAdmin()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Laporan", e -> openFormLaporan()));
        
        sidebar.add(Box.createVerticalGlue());
        
        JButton btnLogout = createNavButton("Logout", e -> logout());
        btnLogout.setBackground(Color.decode("#E11D48")); // Rose Red
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblWelcome = new JLabel("Selamat datang, " + getUserDisplay());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblWelcome, BorderLayout.WEST);
        
        // Header separator line
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E5E7EB")));
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(Color.decode("#F9FAFB")); // Light Gray background
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        cardsPanel.setBackground(Color.decode("#F9FAFB"));
        cardsPanel.add(createStatCard("Total Pendapatan", lblTotalPendapatan, "#10B981")); // Emerald Green
        cardsPanel.add(createStatCard("Total Kamar", lblTotalKamar, "#3B82F6")); // Blue
        cardsPanel.add(createStatCard("Kamar Tersedia", lblKamarTersedia, "#F59E0B")); // Amber
        cardsPanel.add(createStatCard("Kamar Terisi", lblKamarTerisi, "#EF4444")); // Red
        cardsPanel.add(createStatCard("Total Reservasi", lblTotalReservasi, "#8B5CF6")); // Purple
        content.add(cardsPanel, BorderLayout.NORTH);

        // Bar Chart
        barChart = new BarChartPanel(new double[12], new String[]{"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des"}, "Pendapatan Bulanan Tahun Ini");
        content.add(barChart, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#2563EB")); // Slightly lighter blue for buttons
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.addActionListener(action);
        return btn;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String colorHex) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(220, 120));
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style", "arc: 15");
        
        // FlatLaf dropshadow trick using border or just matte border top
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, Color.decode(colorHex)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);
        card.add(lblTitle, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.decode("#1F2937"));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
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

        lblTotalKamar.setText(String.valueOf(total));
        lblKamarTersedia.setText(String.valueOf(tersedia));
        lblKamarTerisi.setText(String.valueOf(terisi));
        lblTotalReservasi.setText(String.valueOf(totalReservasi));
        lblTotalPendapatan.setText("Rp " + String.format("%,.0f", pendapatan));
        
        if (barChart != null) {
            int currentYear = java.time.LocalDate.now().getYear();
            double[] monthly = new dao.PembayaranDAO().getMonthlyRevenue(currentYear);
            barChart.setValues(monthly);
        }
    }

    private void openFormKamar() {
        FormKamar fk = new FormKamar(this);
        fk.setVisible(true);
        loadStats(); // Reload stats when child form closes
    }

    private void openFormUser() {
        FormUser fu = new FormUser(this);
        fu.setVisible(true);
    }

    private void openFormLaporan() {
        FormLaporan fl = new FormLaporan(this);
        fl.setVisible(true);
        loadStats();
    }

    private void openFormPembayaranAdmin() {
        FormPembayaranAdmin fp = new FormPembayaranAdmin(this);
        fp.setVisible(true);
        loadStats();
    }

    private void logout() {
        Session.clear();
        this.dispose();
        new view.login.LoginForm().setVisible(true);
    }
}
