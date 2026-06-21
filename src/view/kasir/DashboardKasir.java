package view.kasir;

import dao.KamarDAO;
import model.Kamar;
import utils.Session;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DashboardKasir extends JFrame {

    private KamarDAO kamarDAO = new KamarDAO();
    private JPanel roomPanel;
    private JScrollPane scrollRoom;

    public DashboardKasir() {
        setTitle("Dashboard Kasir - Hotel Reservation");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.decode("#1E3A8A")); // Deep Blue
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel title = new JLabel("<html><div style='text-align: center;'>Kasir<br>Panel</div></html>", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        sidebar.add(createNavButton("Data Tamu", e -> openFormTamu()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Reservasi", e -> openFormReservasi()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Check-In", e -> openFormCheckIn()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Check-Out", e -> openFormCheckOut()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Pembayaran", e -> openFormPembayaran()));
        
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
        
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E5E7EB")));
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.decode("#F9FAFB"));

        // Quick Actions
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        quickActionsPanel.setBackground(Color.decode("#F9FAFB"));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Aksi Cepat"));
        
        quickActionsPanel.add(createQuickActionButton("Tamu Baru", "#10B981", e -> openFormTamu()));
        quickActionsPanel.add(createQuickActionButton("Reservasi Baru", "#3B82F6", e -> openFormReservasi()));
        quickActionsPanel.add(createQuickActionButton("Proses Check-In", "#F59E0B", e -> openFormCheckIn()));
        quickActionsPanel.add(createQuickActionButton("Proses Check-Out", "#8B5CF6", e -> openFormCheckOut()));

        content.add(quickActionsPanel, BorderLayout.NORTH);
        
        // Visualisasi Denah Kamar
        roomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        roomPanel.setBackground(Color.decode("#F9FAFB"));
        
        scrollRoom = new JScrollPane(roomPanel);
        scrollRoom.setBorder(BorderFactory.createTitledBorder("Denah Kamar (Status Real-time)"));
        scrollRoom.getVerticalScrollBar().setUnitIncrement(16);
        content.add(scrollRoom, BorderLayout.CENTER);
        
        refreshRoomPanel();

        add(content, BorderLayout.CENTER);
    }
    
    private void refreshRoomPanel() {
        roomPanel.removeAll();
        List<Kamar> listKamar = kamarDAO.findAll();
        for (Kamar k : listKamar) {
            roomPanel.add(createRoomCard(k));
        }
        roomPanel.revalidate();
        roomPanel.repaint();
    }
    
    private JPanel createRoomCard(Kamar k) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(150, 100));
        card.putClientProperty("FlatLaf.style", "arc: 15");
        
        String colorHex;
        switch (k.getStatus().toUpperCase()) {
            case "TERSEDIA": colorHex = "#10B981"; break; // Green
            case "TERISI": colorHex = "#EF4444"; break; // Red
            case "DIPESAN": colorHex = "#F59E0B"; break; // Amber
            default: colorHex = "#6B7280"; // Gray
        }
        
        card.setBackground(Color.decode(colorHex));
        
        JLabel lblNo = new JLabel(k.getNomorKamar(), SwingConstants.CENTER);
        lblNo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNo.setForeground(Color.WHITE);
        
        JLabel lblType = new JLabel(k.getTipeKamar() + " - " + k.getStatus(), SwingConstants.CENTER);
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblType.setForeground(Color.WHITE);
        
        card.add(lblNo, BorderLayout.CENTER);
        card.add(lblType, BorderLayout.SOUTH);
        
        // Add border to give some padding
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        return card;
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#2563EB")); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.addActionListener(action);
        return btn;
    }

    private JButton createQuickActionButton(String text, String colorHex, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(160, 60));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode(colorHex));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private String getUserDisplay() {
        User u = Session.getUser();
        return u != null ? u.getUsername() + " (" + u.getRole() + ")" : "Guest";
    }

    private void openFormTamu() {
        FormTamu ft = new FormTamu(this);
        ft.setVisible(true);
        refreshRoomPanel();
    }

    private void openFormReservasi() {
        FormReservasi fr = new FormReservasi(this);
        fr.setVisible(true);
        refreshRoomPanel();
    }

    private void openFormCheckIn() {
        FormCheckIn fci = new FormCheckIn(this);
        fci.setVisible(true);
        refreshRoomPanel();
    }

    private void openFormCheckOut() {
        FormCheckOut fco = new FormCheckOut(this);
        fco.setVisible(true);
        refreshRoomPanel();
    }

    private void openFormPembayaran() {
        FormPembayaran fp = new FormPembayaran(this);
        fp.setVisible(true);
        refreshRoomPanel();
    }

    private void logout() {
        Session.clear();
        this.dispose();
        new view.login.LoginForm().setVisible(true);
    }
}
