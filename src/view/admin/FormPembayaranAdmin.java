package view.admin;

import dao.PembayaranDAO;
import dao.ReservasiDAO;
import dao.KamarDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class FormPembayaranAdmin extends JDialog {
    private PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private JTable table;
    private DefaultTableModel model;

    public FormPembayaranAdmin(JFrame parent) {
        super(parent, "Data Pembayaran", true);
        setSize(800, 450);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","ID Reservasi","Lama Menginap","Total Bayar","Tanggal Bayar"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<model.Pembayaran> list = pembayaranDAO.findAll();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (model.Pembayaran p : list) {
            model.addRow(new Object[]{p.getIdPembayaran(), p.getIdReservasi(), p.getLamaMenginap(), p.getTotalBayar(), p.getTanggalBayar().format(f)});
        }
    }
}
