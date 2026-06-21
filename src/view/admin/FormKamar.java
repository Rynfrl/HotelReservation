package view.admin;

import dao.KamarDAO;
import model.Kamar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        super(parent, "Manajemen Kamar", true);
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
        JLabel lblTitle = new JLabel("Daftar Kamar");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchPanel.add(lblTitle);
        
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari Nomor/Tipe Kamar...");
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
        model = new DefaultTableModel(new Object[]{"ID", "Nomor", "Tipe", "Harga", "Status"}, 0) {
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
                    txtNomor.setText(model.getValueAt(r, 1).toString());
                    txtTipe.setText(model.getValueAt(r, 2).toString());
                    txtHarga.setText(model.getValueAt(r, 3).toString());
                    cbStatus.setSelectedItem(model.getValueAt(r, 4).toString());
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

        JLabel formTitle = new JLabel("Detail Kamar");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(formTitle, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Nomor Kamar:"), gbc);
        gbc.gridy++;
        txtNomor = new JTextField();
        txtNomor.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtNomor, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Tipe Kamar:"), gbc);
        gbc.gridy++;
        txtTipe = new JTextField();
        txtTipe.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtTipe, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridy++;
        txtHarga = new JTextField();
        txtHarga.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtHarga, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Status:"), gbc);
        gbc.gridy++;
        cbStatus = new JComboBox<>(new String[]{"TERSEDIA", "DIPESAN", "TERISI"});
        rightPanel.add(cbStatus, gbc);

        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBackground(Color.decode("#1E3A8A"));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addKamar());
        rightPanel.add(btnAdd, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JButton btnEdit = new JButton("Simpan Perubahan");
        btnEdit.addActionListener(e -> editKamar());
        rightPanel.add(btnEdit, gbc);

        gbc.gridy++;
        JButton btnDelete = new JButton("Hapus Kamar");
        btnDelete.setBackground(Color.decode("#EF4444")); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteKamar());
        rightPanel.add(btnDelete, gbc);

        // Spacer to push everything to top
        gbc.gridy++;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        add(rightPanel, BorderLayout.EAST);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<Kamar> list = kamarDAO.findAll();
        for (Kamar k : list) {
            model.addRow(new Object[]{k.getIdKamar(), k.getNomorKamar(), k.getTipeKamar(), k.getHarga(), k.getStatus()});
        }
        clearForm();
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
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kamar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editKamar() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kamar terlebih dahulu dari tabel.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
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
        if (k == null) return;
        
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
        if (kamarDAO.update(k)) {
            loadTable();
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
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        String status = model.getValueAt(r, 4).toString();
        if ("TERISI".equals(status) || "DIPESAN".equals(status)) {
            JOptionPane.showMessageDialog(this, "Tidak dapat menghapus kamar yang sedang digunakan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus kamar ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (kamarDAO.delete(id)) {
                loadTable();
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
        table.clearSelection();
    }
}
