package view.kasir;

import dao.TamuDAO;
import model.Tamu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormTamu extends JDialog {
    private TamuDAO tamuDAO = new TamuDAO();
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtNama;
    private JTextField txtNoHp;
    private JTextField txtAlamat;
    private JTextField txtSearch;

    public FormTamu(JFrame parent) {
        super(parent, "Data Tamu", true);
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

        JLabel lblNama = new JLabel("Nama:");
        lblNama.setBounds(10,10,100,25);
        form.add(lblNama);
        txtNama = new JTextField();
        txtNama.setBounds(120,10,150,25);
        form.add(txtNama);

        JLabel lblNo = new JLabel("No HP:");
        lblNo.setBounds(300,10,100,25);
        form.add(lblNo);
        txtNoHp = new JTextField();
        txtNoHp.setBounds(400,10,150,25);
        form.add(txtNoHp);

        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setBounds(10,50,100,25);
        form.add(lblAlamat);
        txtAlamat = new JTextField();
        txtAlamat.setBounds(120,50,430,25);
        form.add(txtAlamat);

        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBounds(580,10,100,30);
        btnAdd.addActionListener(e -> addTamu());
        form.add(btnAdd);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBounds(580,50,100,30);
        btnEdit.addActionListener(e -> editTamu());
        form.add(btnEdit);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBounds(580,90,100,30);
        btnDelete.addActionListener(e -> deleteTamu());
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

        model = new DefaultTableModel(new Object[]{"ID","Nama","No HP","Alamat"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    txtNama.setText(model.getValueAt(r,1).toString());
                    txtNoHp.setText(model.getValueAt(r,2).toString());
                    txtAlamat.setText(model.getValueAt(r,3).toString());
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<Tamu> list = tamuDAO.findAll();
        for (Tamu t : list) {
            model.addRow(new Object[]{t.getIdTamu(), t.getNama(), t.getNoHp(), t.getAlamat()});
        }
    }

    private void addTamu() {
        String nama = txtNama.getText().trim();
        String no = txtNoHp.getText().trim();
        String alamat = txtAlamat.getText().trim();
        if (nama.isEmpty() || no.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Tamu t = new Tamu();
        t.setNama(nama);
        t.setNoHp(no);
        t.setAlamat(alamat);
        boolean ok = tamuDAO.save(t);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Tamu berhasil ditambahkan.");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan tamu.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editTamu() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tamu terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        String nama = txtNama.getText().trim();
        String no = txtNoHp.getText().trim();
        String alamat = txtAlamat.getText().trim();
        if (nama.isEmpty() || no.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Tamu t = tamuDAO.findById(id);
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Tamu tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        t.setNama(nama);
        t.setNoHp(no);
        t.setAlamat(alamat);
        boolean ok = tamuDAO.update(t);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Tamu berhasil diupdate.");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update tamu.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTamu() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tamu terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus tamu ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = tamuDAO.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Tamu dihapus.");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tamu.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void search() {
        String k = txtSearch.getText().trim();
        model.setRowCount(0);
        List<Tamu> list = tamuDAO.search(k);
        for (Tamu t : list) {
            model.addRow(new Object[]{t.getIdTamu(), t.getNama(), t.getNoHp(), t.getAlamat()});
        }
    }

    private void clearForm() {
        txtNama.setText("");
        txtNoHp.setText("");
        txtAlamat.setText("");
    }
}
