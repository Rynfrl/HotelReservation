package view.admin;

import dao.PembayaranDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class FormPembayaranAdmin extends JDialog {
    private PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private JTable table;
    private DefaultTableModel model;

    public FormPembayaranAdmin(JFrame parent) {
        super(parent, "Histori Pembayaran", true);
        setSize(850, 500);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.decode("#F9FAFB"));

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.decode("#F9FAFB"));
        JLabel lblTitle = new JLabel("Histori Pembayaran");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.decode("#1F2937"));
        headerPanel.add(lblTitle);
        
        add(headerPanel, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new Object[]{"ID", "ID Reservasi", "Lama Menginap", "Total Bayar", "Tanggal Bayar"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#E5E7EB"), 1));
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel (Refresh Button)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.decode("#F9FAFB"));
        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.putClientProperty("JButton.buttonType", "roundRect");
        btnRefresh.addActionListener(e -> loadTable());
        footerPanel.add(btnRefresh);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<model.Pembayaran> list = pembayaranDAO.findAll();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (model.Pembayaran p : list) {
            model.addRow(new Object[]{p.getIdPembayaran(), p.getIdReservasi(), p.getLamaMenginap(), "Rp " + String.format("%,.0f", p.getTotalBayar()), p.getTanggalBayar().format(f)});
        }
    }
}
