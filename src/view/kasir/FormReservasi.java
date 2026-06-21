package view.kasir;

import dao.KamarDAO;
import dao.TamuDAO;
import dao.ReservasiDAO;
import model.Kamar;
import model.Tamu;
import model.Reservasi;
import service.ReservasiService;
import utils.DateHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FormReservasi extends JDialog {
    private TamuDAO tamuDAO = new TamuDAO();
    private KamarDAO kamarDAO = new KamarDAO();
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private ReservasiService reservasiService = new ReservasiService();

    private JTable tableReservasi;
    private DefaultTableModel modelReservasi;

    private JComboBox<String> cbTamu;
    private JComboBox<String> cbKamar;
    private JTextField txtCheckin;
    private JTextField txtCheckout;

    public FormReservasi(JFrame parent) {
        super(parent, "Reservasi Baru", true);
        setSize(900, 600);
        setLocationRelativeTo(parent);
        initUI();
        loadData();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.decode("#F9FAFB"));

        // Left Panel (Table)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.decode("#F9FAFB"));
        
        JLabel lblTitle = new JLabel("Daftar Reservasi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leftPanel.add(lblTitle, BorderLayout.NORTH);

        modelReservasi = new DefaultTableModel(new Object[]{"ID", "Tamu", "Kamar", "Check-in", "Check-out", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableReservasi = new JTable(modelReservasi);
        tableReservasi.setRowHeight(30);
        tableReservasi.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableReservasi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableReservasi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tableReservasi);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.CENTER);

        // Right Panel (Form area)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.putClientProperty("FlatLaf.style", "arc: 15");
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel formTitle = new JLabel("Buat Reservasi Baru");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(formTitle, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Pilih Tamu:"), gbc);
        gbc.gridy++;
        cbTamu = new JComboBox<>();
        rightPanel.add(cbTamu, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Pilih Kamar (Tersedia):"), gbc);
        gbc.gridy++;
        cbKamar = new JComboBox<>();
        rightPanel.add(cbKamar, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Check-in (yyyy-MM-dd):"), gbc);
        gbc.gridy++;
        txtCheckin = new JTextField();
        txtCheckin.setText(LocalDate.now().toString()); // Auto-fill today
        txtCheckin.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtCheckin, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Check-out (yyyy-MM-dd):"), gbc);
        gbc.gridy++;
        txtCheckout = new JTextField();
        txtCheckout.setText(LocalDate.now().plusDays(1).toString()); // Auto-fill tomorrow
        txtCheckout.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtCheckout, gbc);

        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        JButton btnReserve = new JButton("Simpan Reservasi");
        btnReserve.setBackground(Color.decode("#1E3A8A"));
        btnReserve.setForeground(Color.WHITE);
        btnReserve.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReserve.addActionListener(e -> createReservasi());
        rightPanel.add(btnReserve, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JButton btnRefresh = new JButton("Refresh Data Kamar");
        btnRefresh.addActionListener(e -> loadData());
        rightPanel.add(btnRefresh, gbc);

        // Spacer to push everything to top
        gbc.gridy++;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        add(rightPanel, BorderLayout.EAST);
    }

    private void loadData() {
        cbTamu.removeAllItems();
        List<Tamu> tamuList = tamuDAO.findAll();
        for (Tamu t : tamuList) {
            cbTamu.addItem(t.getIdTamu() + " - " + t.getNama());
        }
        cbKamar.removeAllItems();
        List<Kamar> kamarList = kamarDAO.findAvailable();
        for (Kamar k : kamarList) {
            cbKamar.addItem(k.getIdKamar() + " - " + k.getNomorKamar() + " (" + k.getTipeKamar() + ")");
        }
    }

    private void loadTable() {
        modelReservasi.setRowCount(0);
        List<Reservasi> list = reservasiDAO.findAll();
        for (Reservasi r : list) {
            modelReservasi.addRow(new Object[]{r.getIdReservasi(), r.getIdTamu(), r.getIdKamar(), r.getTanggalCheckin(), r.getTanggalCheckout(), r.getStatus()});
        }
    }

    private void createReservasi() {
        if (cbTamu.getItemCount() == 0 || cbKamar.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Pastikan ada tamu dan kamar tersedia.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            String t = cbTamu.getSelectedItem().toString();
            int idTamu = Integer.parseInt(t.split(" - ")[0]);
            String k = cbKamar.getSelectedItem().toString();
            int idKamar = Integer.parseInt(k.split(" - ")[0]);
            LocalDate checkin = LocalDate.parse(txtCheckin.getText().trim());
            LocalDate checkout = LocalDate.parse(txtCheckout.getText().trim());
            if (!checkout.isAfter(checkin)) {
                JOptionPane.showMessageDialog(this, "Check-out harus lebih besar dari check-in.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Reservasi r = new Reservasi();
            r.setIdTamu(idTamu);
            r.setIdKamar(idKamar);
            r.setTanggalCheckin(checkin);
            r.setTanggalCheckout(checkout);
            r.setStatus("DIPESAN");
            boolean ok = reservasiService.createReservasi(r);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Reservasi berhasil dibuat.");
                loadData();
                loadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal membuat reservasi. Pastikan kamar tersedia dan data valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah atau data tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
