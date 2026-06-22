package view.kasir;

import dao.ReservasiDAO;
import dao.KamarDAO;
import model.Reservasi;
import model.Kamar;
import service.PembayaranService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class FormPembayaran extends JDialog {
    private ReservasiDAO reservasiDAO = new ReservasiDAO();
    private PembayaranService pembayaranService = new PembayaranService();
    private JTable table;
    private DefaultTableModel model;

    private JLabel lblTotalBayar;
    private JTextField txtDibayar;
    private JLabel lblKembalian;
    private JButton btnPay;
    
    private double currentTotal = 0;
    private int currentReservasiId = -1;

    public FormPembayaran(JFrame parent) {
        super(parent, "Proses Pembayaran", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        initUI();
        loadTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.decode("#F9FAFB"));

        // Left Panel (Table)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.decode("#F9FAFB"));
        
        JLabel lblTitle = new JLabel("Daftar Tagihan Reservasi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leftPanel.add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "ID Tamu", "Kamar", "Harga/Malam", "Check-in", "Check-out", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectReservasi();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTable());
        leftPanel.add(btnRefresh, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.CENTER);

        // Right Panel (Payment area)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.putClientProperty("FlatLaf.style", "arc: 15");
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel formTitle = new JLabel("Rincian Pembayaran");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightPanel.add(formTitle, gbc);

        gbc.gridy++;
        rightPanel.add(new JSeparator(), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 5, 0);
        rightPanel.add(new JLabel("Total Tagihan:"), gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        lblTotalBayar = new JLabel("Rp 0");
        lblTotalBayar.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotalBayar.setForeground(Color.decode("#EF4444")); // Red for bill
        rightPanel.add(lblTotalBayar, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 5, 0);
        rightPanel.add(new JLabel("Jumlah Dibayar:"), gbc);
        
        gbc.gridy++;
        txtDibayar = new JTextField();
        txtDibayar.setFont(new Font("Segoe UI", Font.BOLD, 24));
        txtDibayar.putClientProperty("JComponent.roundRect", true);
        txtDibayar.setHorizontalAlignment(JTextField.RIGHT);
        
        // Auto-calculate logic
        txtDibayar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateKembalian(); }
            public void removeUpdate(DocumentEvent e) { calculateKembalian(); }
            public void changedUpdate(DocumentEvent e) { calculateKembalian(); }
        });
        
        // Listen for Enter to trigger Pay
        txtDibayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pay();
                }
            }
        });
        rightPanel.add(txtDibayar, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Kembalian:"), gbc);
        
        gbc.gridy++;
        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblKembalian.setForeground(Color.decode("#10B981")); // Emerald Green
        rightPanel.add(lblKembalian, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 5, 0);
        btnPay = new JButton("Bayar & Cetak Struk");
        btnPay.setBackground(Color.decode("#1E3A8A"));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.setPreferredSize(new Dimension(0, 50));
        btnPay.addActionListener(e -> pay());
        btnPay.setEnabled(false); // Disabled initially
        rightPanel.add(btnPay, gbc);

        // Spacer
        gbc.gridy++;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        add(rightPanel, BorderLayout.EAST);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<Reservasi> list = reservasiDAO.findAll();
        for (Reservasi r : list) {
            // Usually we pay when checking out or before check-in. 
            // In this logic, allow any, but typically it's CHECK_IN or DIPESAN
            model.addRow(new Object[]{r.getIdReservasi(), r.getIdTamu(), r.getIdKamar(), "Rp " + getHarga(r.getIdKamar()), r.getTanggalCheckin(), r.getTanggalCheckout(), r.getStatus()});
        }
        resetPaymentPanel();
    }

    private double getHarga(int idKamar) {
        Kamar k = new KamarDAO().findById(idKamar);
        return k != null ? k.getHarga() : 0.0;
    }

    private void selectReservasi() {
        int r = table.getSelectedRow();
        if (r < 0) {
            resetPaymentPanel();
            return;
        }
        currentReservasiId = Integer.parseInt(model.getValueAt(r, 0).toString());
        currentTotal = pembayaranService.calculateTotal(currentReservasiId);
        lblTotalBayar.setText("Rp " + String.format("%,.0f", currentTotal));
        btnPay.setEnabled(true);
        
        // Auto-focus the payment input field
        txtDibayar.setText("");
        txtDibayar.requestFocus();
    }

    private void calculateKembalian() {
        if (currentTotal == 0) return;
        try {
            String dibayarStr = txtDibayar.getText().replace(",", "").replace(".", "").trim();
            if (dibayarStr.isEmpty()) {
                lblKembalian.setText("Rp 0");
                return;
            }
            double dibayar = Double.parseDouble(dibayarStr);
            double kembalian = dibayar - currentTotal;
            if (kembalian < 0) kembalian = 0;
            lblKembalian.setText("Rp " + String.format("%,.0f", kembalian));
        } catch (NumberFormatException ignored) {
            lblKembalian.setText("Rp 0");
        }
    }

    private void resetPaymentPanel() {
        currentReservasiId = -1;
        currentTotal = 0;
        lblTotalBayar.setText("Rp 0");
        txtDibayar.setText("");
        lblKembalian.setText("Rp 0");
        btnPay.setEnabled(false);
    }

    private void pay() {
        if (currentReservasiId == -1) return;
        double dibayar = 0;
        try {
            String dibayarStr = txtDibayar.getText().replace(",", "").replace(".", "").trim();
            dibayar = Double.parseDouble(dibayarStr);
            if (dibayar < currentTotal) {
                JOptionPane.showMessageDialog(this, "Uang pembayaran kurang!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka nominal pembayaran yang valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = pembayaranService.pay(currentReservasiId);
        if (ok) {
            model.Pembayaran p = pembayaranService.getPembayaran(currentReservasiId);
            Reservasi r = reservasiDAO.findById(currentReservasiId);
            Kamar k = new dao.KamarDAO().findById(r.getIdKamar());
            model.Tamu t = new dao.TamuDAO().findById(r.getIdTamu());
            
            double kembalian = dibayar - currentTotal;
            if (kembalian < 0) kembalian = 0;
            
            String receiptPath = utils.ReceiptGenerator.generateReceipt(p, r, k, t, dibayar, kembalian);
            
            if (receiptPath != null) {
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil disimpan.\nStruk telah dicetak di:\n" + receiptPath, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil disimpan tetapi struk gagal dicetak.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pembayaran atau pembayaran sudah dilakukan sebelumnya.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
