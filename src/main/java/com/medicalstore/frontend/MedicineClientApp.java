package com.medicalstore.frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class MedicineClientApp extends JFrame {

    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField, quantityField;
    private JButton purchaseButton, searchButton;

    public MedicineClientApp() {
        setTitle("Medicine Purchase Panel - Medical Store");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 250, 255));

        // Header
        JLabel header = new JLabel("Medicine Purchase Panel", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setOpaque(true);
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        // Table setup
        String[] cols = {"ID", "Name", "Description", "Price", "Stock"};
        tableModel = new DefaultTableModel(cols, 0);
        medicineTable = new JTable(tableModel);
        medicineTable.setRowHeight(28);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        medicineTable.getTableHeader().setBackground(new Color(41, 128, 185));
        medicineTable.getTableHeader().setForeground(Color.WHITE);
        medicineTable.setSelectionBackground(new Color(255, 228, 181));
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(new Color(245, 250, 255));

        searchField = new JTextField();
        quantityField = new JTextField();

        searchButton = createIconButton("Search", "search-icon.png", new Color(52, 152, 219), Color.WHITE);
        purchaseButton = createIconButton("Purchase", "purchase-icon.png", new Color(39, 174, 96), Color.WHITE);

        formPanel.add(new JLabel("Search by Name:"));
        formPanel.add(searchField);
        formPanel.add(searchButton);
        formPanel.add(new JLabel()); // filler

        formPanel.add(new JLabel("Quantity to Purchase:"));
        formPanel.add(quantityField);
        formPanel.add(purchaseButton);
        formPanel.add(new JLabel()); // filler

        add(formPanel, BorderLayout.SOUTH);

        // Actions
        searchButton.addActionListener(e -> fetchAndDisplayMedicines(searchField.getText().trim()));
        purchaseButton.addActionListener(e -> handlePurchase());

        fetchAndDisplayMedicines("");
        setVisible(true);
    }

    private JButton createIconButton(String text, String iconName, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconName));
            Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconName);
        }

        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));
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

    private void fetchAndDisplayMedicines(String searchQuery) {
        try {
            tableModel.setRowCount(0);
            URL url = new URL("http://localhost:8080/api/medicines");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            String data = json.toString().trim();
            if (data.equals("[]")) return;

            data = data.substring(1, data.length() - 1);
            String[] items = data.split("\\},\\{");

            for (String item : items) {
                if (!item.startsWith("{")) item = "{" + item;
                if (!item.endsWith("}")) item = item + "}";

                item = item.replace("{", "").replace("}", "");
                String[] fields = item.split(",");

                Map<String, String> map = new HashMap<>();
                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    if (keyValue.length == 2) {
                        map.put(keyValue[0].replaceAll("\"", "").trim(), keyValue[1].replaceAll("\"", "").trim());
                    }
                }

                String name = map.get("name") != null ? map.get("name").toLowerCase() : "";
                if (searchQuery.isEmpty() || name.contains(searchQuery.toLowerCase())) {
                    tableModel.addRow(new Object[]{
                            Integer.parseInt(map.get("id")),
                            map.get("name"),
                            map.get("description"),
                            Double.parseDouble(map.get("price")),
                            Integer.parseInt(map.get("stock"))
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching medicines: " + e.getMessage());
        }
    }

    private void handlePurchase() {
        int row = medicineTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine.");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            int currentStock = (int) tableModel.getValueAt(row, 4);
            int quantity = Integer.parseInt(quantityField.getText());

            if (currentStock <= 0) {
                JOptionPane.showMessageDialog(this, "No stock left for this medicine.");
                return;
            }

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number.");
                return;
            }

            if (quantity > currentStock) {
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + currentStock);
                return;
            }

            String urlString = String.format(
                    "http://localhost:8080/api/medicines/purchase?medicineId=%d&quantity=%d",
                    id, quantity);
            URL url = new URL(urlString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Purchase successful!");
                fetchAndDisplayMedicines(searchField.getText().trim());
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String error = br.readLine();
                br.close();
                JOptionPane.showMessageDialog(this, "Purchase failed: " + error);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format. Please enter a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during purchase: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MedicineClientApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
