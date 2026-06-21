package view.admin;

import dao.UserDAO;
import model.User;

import javax.swing.*;
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
        super(parent, "Data User (Kasir)", true);
        setSize(700, 450);
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

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(10,10,100,25);
        form.add(lblUser);
        txtUsername = new JTextField();
        txtUsername.setBounds(120,10,150,25);
        form.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(300,10,100,25);
        form.add(lblPass);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(400,10,150,25);
        form.add(txtPassword);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setBounds(10,50,100,25);
        form.add(lblRole);
        cbRole = new JComboBox<>(new String[]{"KASIR"});
        cbRole.setBounds(120,50,150,25);
        form.add(cbRole);

        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBounds(580,10,80,30);
        btnAdd.addActionListener(e -> addUser());
        form.add(btnAdd);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBounds(580,50,80,30);
        btnEdit.addActionListener(e -> editUser());
        form.add(btnEdit);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBounds(580,90,80,30);
        btnDelete.addActionListener(e -> deleteUser());
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

        model = new DefaultTableModel(new Object[]{"ID","Username","Role"},0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    txtUsername.setText(model.getValueAt(r,1).toString());
                    cbRole.setSelectedItem(model.getValueAt(r,2).toString());
                    txtPassword.setText("");
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<User> list = userDAO.findAll();
        for (User u : list) {
            model.addRow(new Object[]{u.getIdUser(), u.getUsername(), u.getRole()});
        }
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // check duplicate
        if (userDAO.findByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username sudah ada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole(role);
        boolean ok = userDAO.save(u);
        if (ok) {
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan.");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUser() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih user terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User u = userDAO.findByUsername(model.getValueAt(r,1).toString());
        if (u == null) {
            JOptionPane.showMessageDialog(this, "User tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        u.setUsername(username);
        if (!password.isEmpty()) u.setPassword(password);
        u.setRole(role);
        boolean ok = userDAO.update(u);
        if (ok) {
            JOptionPane.showMessageDialog(this, "User berhasil diupdate.");
            loadTable();
            clearForm();
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
        int id = Integer.parseInt(model.getValueAt(r,0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = userDAO.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "User dihapus.");
                loadTable();
                clearForm();
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
    }
}
