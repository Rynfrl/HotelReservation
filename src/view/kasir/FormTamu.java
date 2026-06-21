package view.kasir;

import dao.TamuDAO;
import model.Tamu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setSize(900, 600);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.decode("#F9FAFB"));

        // Left Panel (Table and Search)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.decode("#F9FAFB"));
        
        // Search Header
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.decode("#F9FAFB"));
        JLabel lblTitle = new JLabel("Daftar Tamu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchPanel.add(lblTitle);
        
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari Nama / No HP...");
        txtSearch.putClientProperty("JComponent.roundRect", true);
        
        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(e -> search());
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTable());
        
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{"ID", "Nama Tamu", "No HP", "Alamat"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    txtNama.setText(model.getValueAt(r, 1).toString());
                    txtNoHp.setText(model.getValueAt(r, 2).toString());
                    txtAlamat.setText(model.getValueAt(r, 3).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.CENTER);

        // Right Panel (Form area)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.putClientProperty("FlatLaf.style", "arc: 15");
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel formTitle = new JLabel("Form Data Tamu");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(formTitle, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Nama Tamu:"), gbc);
        gbc.gridy++;
        txtNama = new JTextField();
        txtNama.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtNama, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("No HP:"), gbc);
        gbc.gridy++;
        txtNoHp = new JTextField();
        txtNoHp.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtNoHp, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridy++;
        txtAlamat = new JTextField();
        txtAlamat.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtAlamat, gbc);

        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        JButton btnAdd = new JButton("Tambah Tamu Baru");
        btnAdd.setBackground(Color.decode("#1E3A8A"));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addTamu());
        rightPanel.add(btnAdd, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JButton btnEdit = new JButton("Simpan Perubahan");
        btnEdit.addActionListener(e -> editTamu());
        rightPanel.add(btnEdit, gbc);

        gbc.gridy++;
        JButton btnDelete = new JButton("Hapus Tamu");
        btnDelete.setBackground(Color.decode("#EF4444")); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteTamu());
        rightPanel.add(btnDelete, gbc);

        // Spacer to push everything to top
        gbc.gridy++;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        add(rightPanel, BorderLayout.EAST);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<Tamu> list = tamuDAO.findAll();
        for (Tamu t : list) {
            model.addRow(new Object[]{t.getIdTamu(), t.getNama(), t.getNoHp(), t.getAlamat()});
        }
        clearForm();
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
        if (tamuDAO.save(t)) {
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan tamu.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editTamu() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tamu terlebih dahulu dari tabel.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        String nama = txtNama.getText().trim();
        String no = txtNoHp.getText().trim();
        String alamat = txtAlamat.getText().trim();
        if (nama.isEmpty() || no.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Tamu t = tamuDAO.findById(id);
        if (t == null) return;
        
        t.setNama(nama);
        t.setNoHp(no);
        t.setAlamat(alamat);
        if (tamuDAO.update(t)) {
            loadTable();
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
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus tamu ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (tamuDAO.delete(id)) {
                loadTable();
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
        table.clearSelection();
    }
}
