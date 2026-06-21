package view.kasir;

import dao.ReservasiDAO;
import dao.KamarDAO;
import dao.TamuDAO;
import model.Reservasi;
import model.Kamar;
import model.Tamu;
import service.PembayaranService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormPembayaran extends JDialog {
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private PembayaranService pembayaranService = new PembayaranService();
    private JTable table;
    private DefaultTableModel model;

    public FormPembayaran(JFrame parent) {
        super(parent, "Pembayaran", true);
        setSize(900, 500);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Tamu","Nomor Kamar","Harga","Checkin","Checkout","Status"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnCalculate = new JButton("Hitung Total");
        btnCalculate.addActionListener(e -> calculate());
        JButton btnPay = new JButton("Bayar");
        btnPay.addActionListener(e -> pay());
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTable());
        bottom.add(btnCalculate);
        bottom.add(btnPay);
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<Reservasi> list = reservasiDAO.findAll();
        for (Reservasi r : list) {
            model.addRow(new Object[]{r.getIdReservasi(), r.getIdTamu(), r.getIdKamar(), getHarga(r.getIdKamar()), r.getTanggalCheckin(), r.getTanggalCheckout(), r.getStatus()});
        }
    }

    private double getHarga(int idKamar) {
        Kamar k = new KamarDAO().findById(idKamar);
        return k != null ? k.getHarga() : 0.0;
    }

    private void calculate() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih reservasi terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        double total = pembayaranService.calculateTotal(id);
        JOptionPane.showMessageDialog(this, "Total bayar: Rp " + String.format("%,.2f", total));
    }

    private void pay() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih reservasi terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        // ensure status is CHECK_IN or DIPESAN? allow payment for any
        boolean ok = pembayaranService.pay(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil disimpan.");
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pembayaran.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
