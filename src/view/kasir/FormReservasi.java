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
        super(parent, "Reservasi", true);
        setSize(900, 550);
        setLocationRelativeTo(parent);
        initUI();
        loadData();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new GridLayout(2,1));
        JPanel form = new JPanel(null);
        form.setPreferredSize(new Dimension(0,120));

        JLabel lblTamu = new JLabel("Pilih Tamu:");
        lblTamu.setBounds(10,10,100,25);
        form.add(lblTamu);
        cbTamu = new JComboBox<>();
        cbTamu.setBounds(120,10,250,25);
        form.add(cbTamu);

        JLabel lblKamar = new JLabel("Pilih Kamar:");
        lblKamar.setBounds(400,10,100,25);
        form.add(lblKamar);
        cbKamar = new JComboBox<>();
        cbKamar.setBounds(510,10,200,25);
        form.add(cbKamar);

        JLabel lblCheckin = new JLabel("Check-in (yyyy-MM-dd):");
        lblCheckin.setBounds(10,50,150,25);
        form.add(lblCheckin);
        txtCheckin = new JTextField();
        txtCheckin.setBounds(170,50,150,25);
        form.add(txtCheckin);

        JLabel lblCheckout = new JLabel("Check-out (yyyy-MM-dd):");
        lblCheckout.setBounds(350,50,150,25);
        form.add(lblCheckout);
        txtCheckout = new JTextField();
        txtCheckout.setBounds(510,50,150,25);
        form.add(txtCheckout);

        JButton btnReserve = new JButton("Buat Reservasi");
        btnReserve.setBounds(700,10,150,30);
        btnReserve.addActionListener(e -> createReservasi());
        form.add(btnReserve);

        top.add(form);

        add(top, BorderLayout.NORTH);

        modelReservasi = new DefaultTableModel(new Object[]{"ID","Tamu","Kamar","Checkin","Checkout","Status"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableReservasi = new JTable(modelReservasi);
        add(new JScrollPane(tableReservasi), BorderLayout.CENTER);
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
