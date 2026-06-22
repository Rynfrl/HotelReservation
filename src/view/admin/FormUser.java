package view.admin;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormUser extends JDialog {
    private UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JTextField txtSearch;

    public FormUser(JFrame parent) {
        super(parent, "Manajemen User", true);
        setSize(800, 550);
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
        JLabel lblTitle = new JLabel("Daftar User");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchPanel.add(lblTitle);
        
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari Username/Role...");
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
        model = new DefaultTableModel(new Object[]{"ID", "Username", "Role"}, 0) {
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
                    txtUsername.setText(model.getValueAt(r, 1).toString());
                    cbRole.setSelectedItem(model.getValueAt(r, 2).toString());
                    txtPassword.setText("");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.CENTER);

        // Right Panel (Form area)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(280, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.putClientProperty("FlatLaf.style", "arc: 15");
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel formTitle = new JLabel("Detail User");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(formTitle, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        txtUsername = new JTextField();
        txtUsername.putClientProperty("JComponent.roundRect", true);
        rightPanel.add(txtUsername, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JComponent.roundRect", true);
        txtPassword.putClientProperty("JTextField.showRevealButton", true);
        rightPanel.add(txtPassword, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Role:"), gbc);
        gbc.gridy++;
        cbRole = new JComboBox<>(new String[]{"KASIR", "ADMIN"});
        rightPanel.add(cbRole, gbc);

        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        JButton btnAdd = new JButton("Tambah User");
        btnAdd.setBackground(Color.decode("#1E3A8A"));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addUser());
        rightPanel.add(btnAdd, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JButton btnEdit = new JButton("Simpan Perubahan");
        btnEdit.addActionListener(e -> editUser());
        rightPanel.add(btnEdit, gbc);

        gbc.gridy++;
        JButton btnDelete = new JButton("Hapus User");
        btnDelete.setBackground(Color.decode("#EF4444")); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteUser());
        rightPanel.add(btnDelete, gbc);

        // Spacer to push everything to top
        gbc.gridy++;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        add(rightPanel, BorderLayout.EAST);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<User> list = userDAO.findAll();
        for (User u : list) {
            model.addRow(new Object[]{u.getIdUser(), u.getUsername(), u.getRole()});
        }
        clearForm();
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userDAO.findByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username sudah ada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(utils.PasswordUtil.hashSHA256(password));
        u.setRole(role);
        if (userDAO.save(u)) {
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUser() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih user terlebih dahulu dari tabel.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User u = userDAO.findByUsername(model.getValueAt(r, 1).toString());
        if (u == null) return;
        
        u.setUsername(username);
        if (!password.isEmpty()) u.setPassword(utils.PasswordUtil.hashSHA256(password));
        u.setRole(role);
        
        if (userDAO.update(u)) {
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih user terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.delete(id)) {
                loadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void search() {
        String k = txtSearch.getText().trim();
        model.setRowCount(0);
        List<User> list = userDAO.findAll();
        for (User u : list) {
            if (u.getUsername().toLowerCase().contains(k.toLowerCase()) || u.getRole().toLowerCase().contains(k.toLowerCase())) {
                model.addRow(new Object[]{u.getIdUser(), u.getUsername(), u.getRole()});
            }
        }
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(0);
        table.clearSelection();
    }
}
