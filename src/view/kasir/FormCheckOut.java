package view.kasir;

import dao.ReservasiDAO;
import model.Reservasi;
import service.ReservasiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormCheckOut extends JDialog {
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private ReservasiService reservasiService = new ReservasiService();

    private JTable table;
    private DefaultTableModel model;

    public FormCheckOut(JFrame parent) {
        super(parent, "Check-Out", true);
        setSize(800, 450);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Tamu","Kamar","Checkin","Checkout","Status"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnCheckOut = new JButton("Check-Out");
        btnCheckOut.addActionListener(e -> doCheckOut());
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTable());
        bottom.add(btnCheckOut);
        bottom.add(btnRefresh);
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
            JOptionPane.showMessageDialog(this, "Pilih reservasi yang akan check-out.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        boolean ok = reservasiService.checkOut(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Check-Out berhasil.");
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal check-out.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
