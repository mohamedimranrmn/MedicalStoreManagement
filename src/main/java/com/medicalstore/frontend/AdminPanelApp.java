package com.medicalstore.frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class AdminPanelApp extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, priceField, stockField;
    private JTextArea descriptionArea;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    private JTabbedPane tabbedPane;

    public AdminPanelApp() {
        setTitle("Admin Panel - Medical Store");
        setSize(1000, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 250, 255));

        // Header
        JLabel header = new JLabel("Medical Store Admin Panel", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setBorder(new EmptyBorder(20, 10, 10, 10));
        header.setOpaque(true);
        header.setBackground(new Color(30, 144, 255));
        header.setForeground(Color.WHITE);
        add(header, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // Admin Panel - Medicine Management
        JPanel medicinePanel = createMedicinePanel();
        tabbedPane.addTab("Manage Medicines", medicinePanel);

        // Admin Panel - Sales Management
        SalesPanel salesPanel = new SalesPanel();
        tabbedPane.addTab("Sales Records", salesPanel);

        setVisible(true);
    }

    private JPanel createMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for medicines
        model = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Price", "Stock"}, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(65, 105, 225));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 228, 181));
        JScrollPane tableScroll = new JScrollPane(table);
        panel.add(tableScroll, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBackground(new Color(245, 250, 255));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        nameField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        priceField = new JTextField();
        stockField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Stock:"));
        formPanel.add(stockField);

        addButton = createButton("Add", "add-icon.png", new Color(46, 204, 113), Color.WHITE);
        updateButton = createButton("Update", "update-icon.png", new Color(241, 196, 15), Color.WHITE);
        deleteButton = createButton("Delete", "delete-icon.png", new Color(231, 76, 60), Color.WHITE);
        refreshButton = createButton("Refresh", "refresh-icon.png", new Color(52, 152, 219), Color.WHITE);

        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);
        formPanel.add(refreshButton);

        panel.add(formPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addMedicine());
        updateButton.addActionListener(e -> updateMedicine());
        deleteButton.addActionListener(e -> deleteMedicine());
        refreshButton.addActionListener(e -> fetchMedicines());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 1).toString());
                descriptionArea.setText(model.getValueAt(row, 2).toString());
                priceField.setText(model.getValueAt(row, 3).toString());
                stockField.setText(model.getValueAt(row, 4).toString());
            }
        });

        fetchMedicines();
        return panel;
    }

    private void fetchMedicines() {
        model.setRowCount(0);
        try {
            URL url = new URL("http://localhost:8080/api/medicines");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = in.readLine();
            in.close();

            if (response != null && !response.equals("[]")) {
                response = response.substring(1, response.length() - 1);
                String[] items = response.split("\\},\\{");

                for (String item : items) {
                    item = item.replace("{", "").replace("}", "");
                    String[] fields = item.split(",");
                    Vector<String> row = new Vector<>();
                    for (String field : fields) {
                        String[] kv = field.split(":");
                        row.add(kv[1].replace("\"", "").trim());
                    }
                    model.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching medicines.");
        }
    }

    private void addMedicine() {
        try {
            String name = nameField.getText();
            String description = descriptionArea.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            String json = String.format(
                    "{\"name\":\"%s\",\"description\":\"%s\",\"price\":%.2f,\"stock\":%d}",
                    name, description, price, stock
            );

            URL url = new URL("http://localhost:8080/api/medicines");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            OutputStream os = con.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK || con.getResponseCode() == 201) {
                fetchMedicines();
                clearFields();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding medicine.");
        }
    }

    private void updateMedicine() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine to update.");
            return;
        }

        try {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String name = nameField.getText();
            String description = descriptionArea.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            String json = String.format(
                    "{\"name\":\"%s\",\"description\":\"%s\",\"price\":%.2f,\"stock\":%d}",
                    name, description, price, stock
            );

            URL url = new URL("http://localhost:8080/api/medicines/" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            OutputStream os = con.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                fetchMedicines();
                clearFields();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating medicine.");
        }
    }

    private void deleteMedicine() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            URL url = new URL("http://localhost:8080/api/medicines/" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK || con.getResponseCode() == 204) {
                fetchMedicines();
                clearFields();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting medicine.");
        }
    }

    private void clearFields() {
        nameField.setText("");
        descriptionArea.setText("");
        priceField.setText("");
        stockField.setText("");
    }

    private JButton createButton(String text, String icon, Color bgColor, Color textColor) {
        JButton button = new JButton(text);

        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getResource("/icons/" + icon));
            Image scaledImage = rawIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.out.println("Icon not found: " + icon);
        }

        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));
        button.setRolloverEnabled(true);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(10);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPanelApp::new);
    }
}
