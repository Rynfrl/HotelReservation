package view.admin;

import dao.ReservasiDAO;
import dao.PembayaranDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class FormLaporan extends JDialog {
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private PembayaranDAO pembayaranDAO = new PembayaranDAO();

    private JTable tableReservasi;
    private DefaultTableModel modelReservasi;

    private JTable tablePembayaran;
    private DefaultTableModel modelPembayaran;

    private JTextField txtStart;
    private JTextField txtEnd;

    public FormLaporan(JFrame parent) {
        super(parent, "Laporan Manajerial", true);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        initUI();
        loadAll();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.decode("#F9FAFB"));

        // Top Filter Panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(Color.WHITE);
        top.putClientProperty("FlatLaf.style", "arc: 10");
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E7EB"), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblFilter = new JLabel("Filter Rentang Tanggal (yyyy-MM-dd):");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        top.add(lblFilter);

        txtStart = new JTextField(12);
        txtStart.putClientProperty("JTextField.placeholderText", "Start Date");
        txtStart.putClientProperty("JComponent.roundRect", true);
        
        txtEnd = new JTextField(12);
        txtEnd.putClientProperty("JTextField.placeholderText", "End Date");
        txtEnd.putClientProperty("JComponent.roundRect", true);

        JButton btnFilter = new JButton("Filter");
        btnFilter.setBackground(Color.decode("#1E3A8A"));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.putClientProperty("JButton.buttonType", "roundRect");
        btnFilter.addActionListener(e -> filter());

        JButton btnAll = new JButton("Tampilkan Semua");
        btnAll.putClientProperty("JButton.buttonType", "roundRect");
        btnAll.addActionListener(e -> loadAll());

        top.add(txtStart);
        top.add(new JLabel("sampai"));
        top.add(txtEnd);
        top.add(btnFilter);
        top.add(btnAll);
        
        add(top, BorderLayout.NORTH);

        // Center Panel with Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Tab 1: Reservasi
        modelReservasi = new DefaultTableModel(new Object[]{"ID Reservasi", "ID Tamu", "ID Kamar", "Checkin", "Checkout", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableReservasi = createStyledTable(modelReservasi);
        JPanel pnlReservasi = new JPanel(new BorderLayout());
        pnlReservasi.add(new JScrollPane(tableReservasi), BorderLayout.CENTER);
        tabbedPane.addTab("Data Reservasi", pnlReservasi);

        // Tab 2: Pembayaran
        modelPembayaran = new DefaultTableModel(new Object[]{"ID Pembayaran", "ID Reservasi", "Lama Menginap", "Total Bayar", "Tanggal Bayar"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePembayaran = createStyledTable(modelPembayaran);
        JPanel pnlPembayaran = new JPanel(new BorderLayout());
        pnlPembayaran.add(new JScrollPane(tablePembayaran), BorderLayout.CENTER);
        tabbedPane.addTab("Data Pembayaran", pnlPembayaran);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.decode("#F9FAFB"));
        
        JButton btnTotalPendapatan = new JButton("Hitung Total Pendapatan");
        btnTotalPendapatan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTotalPendapatan.setBackground(Color.decode("#10B981")); // Emerald Green
        btnTotalPendapatan.setForeground(Color.WHITE);
        btnTotalPendapatan.putClientProperty("JButton.buttonType", "roundRect");
        btnTotalPendapatan.addActionListener(e -> showTotalPendapatan());
        
        bottom.add(btnTotalPendapatan);
        add(bottom, BorderLayout.SOUTH);
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setRowHeight(35);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tbl;
    }

    private void loadAll() {
        modelReservasi.setRowCount(0);
        modelPembayaran.setRowCount(0);
        
        List<model.Reservasi> rlist = reservasiDAO.findAll();
        for (model.Reservasi r : rlist) {
            modelReservasi.addRow(new Object[]{r.getIdReservasi(), r.getIdTamu(), r.getIdKamar(), r.getTanggalCheckin(), r.getTanggalCheckout(), r.getStatus()});
        }
        
        List<model.Pembayaran> plist = pembayaranDAO.findAll();
        for (model.Pembayaran p : plist) {
            modelPembayaran.addRow(new Object[]{p.getIdPembayaran(), p.getIdReservasi(), p.getLamaMenginap(), "Rp " + String.format("%,.0f", p.getTotalBayar()), p.getTanggalBayar()});
        }
    }

    private void filter() {
        String s = txtStart.getText().trim();
        String e = txtEnd.getText().trim();
        if (s.isEmpty() || e.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan tanggal Start dan End dengan format yyyy-MM-dd.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            Date start = Date.valueOf(LocalDate.parse(s));
            Date end = Date.valueOf(LocalDate.parse(e));
            
            modelReservasi.setRowCount(0);
            List<model.Reservasi> list = reservasiDAO.findByDateRange(start, end);
            for (model.Reservasi r : list) {
                modelReservasi.addRow(new Object[]{r.getIdReservasi(), r.getIdTamu(), r.getIdKamar(), r.getTanggalCheckin(), r.getTanggalCheckout(), r.getStatus()});
            }
            
            // To filter pembayaran by date we would need a method in PembayaranDAO, 
            // assuming it's not strictly required in the user's prompt or we only filter reservations here.
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah. Gunakan yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTotalPendapatan() {
        double total = pembayaranDAO.totalPendapatan();
        JOptionPane.showMessageDialog(this, "Total pendapatan dari semua transaksi:\nRp " + String.format("%,.0f", total), "Total Pendapatan", JOptionPane.INFORMATION_MESSAGE);
    }
}
