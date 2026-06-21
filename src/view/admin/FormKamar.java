package view.admin;

import dao.KamarDAO;
import model.Kamar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormKamar extends JDialog {
    private KamarDAO kamarDAO = new KamarDAO();
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtNomor;
    private JTextField txtTipe;
    private JTextField txtHarga;
    private JComboBox<String> cbStatus;
    private JTextField txtSearch;

    public FormKamar(JFrame parent) {
        super(parent, "Data Kamar", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel form = new JPanel(null);
        form.setPreferredSize(new Dimension(0,120));

        JLabel lblNomor = new JLabel("Nomor Kamar:");
        lblNomor.setBounds(10,10,100,25);
        form.add(lblNomor);
        txtNomor = new JTextField();
        txtNomor.setBounds(120,10,150,25);
        form.add(txtNomor);

        JLabel lblTipe = new JLabel("Tipe Kamar:");
        lblTipe.setBounds(300,10,100,25);
        form.add(lblTipe);
        txtTipe = new JTextField();
        txtTipe.setBounds(400,10,150,25);
        form.add(txtTipe);

        JLabel lblHarga = new JLabel("Harga:");
        lblHarga.setBounds(10,50,100,25);
        form.add(lblHarga);
        txtHarga = new JTextField();
        txtHarga.setBounds(120,50,150,25);
        form.add(txtHarga);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setBounds(300,50,100,25);
        form.add(lblStatus);
        cbStatus = new JComboBox<>(new String[]{"TERSEDIA","DIPESAN","TERISI"});
        cbStatus.setBounds(400,50,150,25);
        form.add(cbStatus);

        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBounds(580,10,100,30);
        btnAdd.addActionListener(e -> addKamar());
        form.add(btnAdd);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBounds(580,50,100,30);
        btnEdit.addActionListener(e -> editKamar());
        form.add(btnEdit);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBounds(580,90,100,30);
        btnDelete.addActionListener(e -> deleteKamar());
        form.add(btnDelete);

        top.add(form, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(e -> search());
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTable());
        searchPanel.add(new JLabel("Cari:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        top.add(searchPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID","Nomor","Tipe","Harga","Status"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    txtNomor.setText(model.getValueAt(r,1).toString());
                    txtTipe.setText(model.getValueAt(r,2).toString());
                    txtHarga.setText(model.getValueAt(r,3).toString());
                    cbStatus.setSelectedItem(model.getValueAt(r,4).toString());
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<Kamar> list = kamarDAO.findAll();
        for (Kamar k : list) {
            model.addRow(new Object[]{k.getIdKamar(), k.getNomorKamar(), k.getTipeKamar(), k.getHarga(), k.getStatus()});
        }
    }

    private void addKamar() {
        String nomor = txtNomor.getText().trim();
        String tipe = txtTipe.getText().trim();
        String hargaStr = txtHarga.getText().trim();
        String status = cbStatus.getSelectedItem().toString();
        if (nomor.isEmpty() || tipe.isEmpty() || hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus angka.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // check duplicate nomor
        List<Kamar> existing = kamarDAO.search(nomor);
        for (Kamar k : existing) {
            if (k.getNomorKamar().equalsIgnoreCase(nomor)) {
                JOptionPane.showMessageDialog(this, "Nomor kamar tidak boleh duplikat.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        Kamar k = new Kamar();
        k.setNomorKamar(nomor);
        k.setTipeKamar(tipe);
        k.setHarga(harga);
        k.setStatus(status);
        boolean ok = kamarDAO.save(k);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Kamar berhasil ditambahkan.");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kamar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editKamar() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kamar terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        String nomor = txtNomor.getText().trim();
        String tipe = txtTipe.getText().trim();
        String hargaStr = txtHarga.getText().trim();
        String status = cbStatus.getSelectedItem().toString();
        if (nomor.isEmpty() || tipe.isEmpty() || hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus angka.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Kamar k = kamarDAO.findById(id);
        if (k == null) {
            JOptionPane.showMessageDialog(this, "Kamar tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // prevent changing nomor to duplicate
        List<Kamar> existing = kamarDAO.search(nomor);
        for (Kamar exK : existing) {
            if (exK.getNomorKamar().equalsIgnoreCase(nomor) && exK.getIdKamar() != id) {
                JOptionPane.showMessageDialog(this, "Nomor kamar tidak boleh duplikat.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        k.setNomorKamar(nomor);
        k.setTipeKamar(tipe);
        k.setHarga(harga);
        k.setStatus(status);
        boolean ok = kamarDAO.update(k);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Kamar berhasil diupdate.");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update kamar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteKamar() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kamar terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        String status = model.getValueAt(r,4).toString();
        if ("TERISI".equals(status) || "DIPESAN".equals(status)) {
            JOptionPane.showMessageDialog(this, "Tidak dapat menghapus kamar yang sedang digunakan atau dipesan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus kamar ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = kamarDAO.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Kamar dihapus.");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kamar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void search() {
        String k = txtSearch.getText().trim();
        model.setRowCount(0);
        List<Kamar> list = kamarDAO.search(k);
        for (Kamar kamar : list) {
            model.addRow(new Object[]{kamar.getIdKamar(), kamar.getNomorKamar(), kamar.getTipeKamar(), kamar.getHarga(), kamar.getStatus()});
        }
    }

    private void clearForm() {
        txtNomor.setText("");
        txtTipe.setText("");
        txtHarga.setText("");
        cbStatus.setSelectedIndex(0);
    }
}
