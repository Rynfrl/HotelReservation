package view.admin;

import dao.ReservasiDAO;
import dao.PembayaranDAO;

import javax.swing.*;
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
        super(parent, "Laporan", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        initUI();
        loadAll();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Filter Tanggal (yyyy-MM-dd)"));
        txtStart = new JTextField(10);
        txtEnd = new JTextField(10);
        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> filter());
        JButton btnAll = new JButton("Semua");
        btnAll.addActionListener(e -> loadAll());
        top.add(new JLabel("Start:"));
        top.add(txtStart);
        top.add(new JLabel("End:"));
        top.add(txtEnd);
        top.add(btnFilter);
        top.add(btnAll);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2,1));
        modelReservasi = new DefaultTableModel(new Object[]{"ID","ID Tamu","ID Kamar","Checkin","Checkout","Status"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableReservasi = new JTable(modelReservasi);
        center.add(new JScrollPane(tableReservasi));

        modelPembayaran = new DefaultTableModel(new Object[]{"ID","ID Reservasi","Lama","Total","Tanggal Bayar"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePembayaran = new JTable(modelPembayaran);
        center.add(new JScrollPane(tablePembayaran));

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnTotalPendapatan = new JButton("Total Pendapatan");
        btnTotalPendapatan.addActionListener(e -> showTotalPendapatan());
        bottom.add(btnTotalPendapatan);
        add(bottom, BorderLayout.SOUTH);
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
            modelPembayaran.addRow(new Object[]{p.getIdPembayaran(), p.getIdReservasi(), p.getLamaMenginap(), p.getTotalBayar(), p.getTanggalBayar()});
        }
    }

    private void filter() {
        String s = txtStart.getText().trim();
        String e = txtEnd.getText().trim();
        if (s.isEmpty() || e.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan tanggal start dan end.", "Info", JOptionPane.INFORMATION_MESSAGE);
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTotalPendapatan() {
        double total = pembayaranDAO.totalPendapatan();
        JOptionPane.showMessageDialog(this, "Total pendapatan: Rp " + String.format("%,.2f", total));
    }
}
