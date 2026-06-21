package view.kasir;

import dao.ReservasiDAO;
import model.Reservasi;
import service.ReservasiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormCheckOut extends JDialog {
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private ReservasiService reservasiService = new ReservasiService();

    private JTable table;
    private DefaultTableModel model;

    public FormCheckOut(JFrame parent) {
        super(parent, "Proses Check-Out", true);
        setSize(850, 500);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.decode("#F9FAFB"));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.decode("#F9FAFB"));
        JLabel lblTitle = new JLabel("Daftar Tamu Siap Check-Out");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        top.add(lblTitle);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID Reservasi", "ID Tamu", "ID Kamar", "Check-in", "Check-out", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setBackground(Color.decode("#F9FAFB"));
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.putClientProperty("JButton.buttonType", "roundRect");
        btnRefresh.addActionListener(e -> loadTable());
        
        JButton btnCheckOut = new JButton("Proses Check-Out");
        btnCheckOut.setBackground(Color.decode("#8B5CF6")); // Purple
        btnCheckOut.setForeground(Color.WHITE);
        btnCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckOut.putClientProperty("JButton.buttonType", "roundRect");
        btnCheckOut.addActionListener(e -> doCheckOut());
        
        bottom.add(btnRefresh);
        bottom.add(btnCheckOut);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<Reservasi> list = reservasiDAO.findAll();
        for (Reservasi r : list) {
            if ("CHECK_IN".equals(r.getStatus())) {
                model.addRow(new Object[]{r.getIdReservasi(), r.getIdTamu(), r.getIdKamar(), r.getTanggalCheckin(), r.getTanggalCheckout(), r.getStatus()});
            }
        }
    }

    private void doCheckOut() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih reservasi yang akan di-check-out dari tabel.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        boolean ok = reservasiService.checkOut(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Check-Out berhasil. Terima kasih!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal check-out.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
