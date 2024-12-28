package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class demoM6 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SewaMobilGUI().setVisible(true));
    }
}

class SewaMobilGUI extends JFrame {
    private JTextField namaField, asalField, tujuanField, hargaField, diskonField;
    private JComboBox<String> jenisMobilComboBox;
    private DefaultTableModel tableModel;
    private JPanel tablePanel;
    private JLabel imageLabel;
    private File selectedImageFile;

    public SewaMobilGUI() {
        setTitle("Sewa Mobil Murah Malang");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        Color bgColor = new Color(10, 25, 49); // Elegant dark blue background
        Color buttonColor = new Color(0, 102, 204); // Deep blue for buttons
        Color textColor = Color.WHITE;

        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        inputPanel.setBackground(bgColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel[] labels = {
                new JLabel("Nama Penyewa:"),
                new JLabel("Asal:"),
                new JLabel("Tujuan:"),
                new JLabel("Harga Sewa (Rp):"),
                new JLabel("Diskon (%):"),
                new JLabel("Jenis Mobil:"),
                new JLabel("Gambar:")
        };

        JTextField[] fields = {
                namaField = new JTextField(), asalField = new JTextField(),
                tujuanField = new JTextField(), hargaField = new JTextField(),
                diskonField = new JTextField()
        };

        for (int i = 0; i < fields.length; i++) {
            labels[i].setForeground(textColor);
            inputPanel.add(labels[i]);
            inputPanel.add(fields[i]);
        }

        // Dropdown untuk jenis mobil
        jenisMobilComboBox = new JComboBox<>(new String[]{"LCGC", "SUV", "MVP"});
        inputPanel.add(labels[5]);
        inputPanel.add(jenisMobilComboBox);

        // Gambar upload field
        imageLabel = new JLabel("Belum ada gambar yang dipilih");
        imageLabel.setForeground(textColor);
        JButton uploadButton = new JButton("Unggah Gambar");
        uploadButton.setBackground(buttonColor);
        uploadButton.setForeground(textColor);
        uploadButton.addActionListener(e -> uploadImage());
        inputPanel.add(labels[6]);
        inputPanel.add(uploadButton);

        JButton submitButton = new JButton("Tambah");
        JButton clearButton = new JButton("Hapus");
        for (JButton button : new JButton[]{submitButton, clearButton}) {
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            inputPanel.add(button);
        }

        add(inputPanel, BorderLayout.NORTH);

        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(bgColor);
        tablePanel.setVisible(false);

        tableModel = new DefaultTableModel(new Object[]{"Nama", "Asal", "Tujuan", "Harga Sewa", "Diskon", "Setelah Diskon", "Jenis Mobil", "Gambar"}, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(100); // Tinggi baris untuk menyesuaikan gambar
        table.setBackground(Color.WHITE);

        // Menambahkan cell renderer untuk kolom gambar
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel();
                    label.setIcon((ImageIcon) value);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                }
                return new JLabel(value != null ? value.toString() : "");
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (column == 7) { // Kolom gambar
                    ImageIcon icon = (ImageIcon) tableModel.getValueAt(row, column);
                    if (icon != null) {
                        JLabel label = new JLabel(icon);
                        JOptionPane.showMessageDialog(null, label, "Gambar", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        });
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> submitAction());
        clearButton.addActionListener(e -> clearFieldsAndTable());
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            imageLabel.setText(selectedImageFile.getName());
            JOptionPane.showMessageDialog(this, "Gambar berhasil dipilih: " + selectedImageFile.getName());
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada gambar yang dipilih.");
        }
    }

    private void submitAction() {
        try {
            String nama = namaField.getText().trim();
            String asal = asalField.getText().trim();
            String tujuan = tujuanField.getText().trim();
            double harga = Double.parseDouble(hargaField.getText().replaceAll("[^0-9]", ""));
            double diskon = Double.parseDouble(diskonField.getText().replaceAll("[^0-9.]", ""));
            String jenisMobil = (String) jenisMobilComboBox.getSelectedItem();

            if (nama.isEmpty() || asal.isEmpty() || tujuan.isEmpty() || harga <= 0 || diskon < 0 || selectedImageFile == null) {
                JOptionPane.showMessageDialog(this, "Semua kolom harus diisi dengan benar dan gambar harus dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Menghitung total setelah diskon
            double total = harga - (harga * diskon / 100);

            // Membuat objek ImageIcon untuk gambar yang diunggah
            ImageIcon imageIcon = new ImageIcon(selectedImageFile.getPath());

            // Menambahkan data baru ke dalam tabel
            tableModel.addRow(new Object[]{
                    nama, asal, tujuan, String.format("Rp %,d", (long) harga), String.format("%.2f%%", diskon),
                    String.format("Rp %,d", (long) total), jenisMobil, imageIcon
            });

            // Menyimpan data dalam format JSON ke file
            String jsonData = createJsonString(nama, asal, tujuan, harga, diskon, total, jenisMobil);
            saveJsonToFile(jsonData);

            // Menampilkan panel tabel
            tablePanel.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Masukkan angka valid untuk harga dan diskon!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String createJsonString(String nama, String asal, String tujuan, double harga, double diskon, double total, String jenisMobil) {
        return String.format(
                "{ \"nama\": \"%s\", \"asal\": \"%s\", \"tujuan\": \"%s\", \"harga\": %.2f, \"diskon\": %.2f, \"total\": %.2f, \"jenisMobil\": \"%s\" }",
                nama, asal, tujuan, harga, diskon, total, jenisMobil
        );
    }

    private void saveJsonToFile(String jsonData) {
        try (FileWriter fileWriter = new FileWriter("data.json")) {
            fileWriter.write(jsonData);
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke file JSON.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke file JSON.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFieldsAndTable() {
        for (JTextField field : new JTextField[]{namaField, asalField, tujuanField, hargaField, diskonField}) {
            field.setText("");
        }
        jenisMobilComboBox.setSelectedIndex(0);
        selectedImageFile = null;
        imageLabel.setText("Belum ada gambar yang dipilih");
        tableModel.setRowCount(0);
        tablePanel.setVisible(false);
    }
}
