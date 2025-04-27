package com.medicalstore.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

public class SalesPanel extends JPanel {
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private Map<Integer, String> medicineIdToName = new HashMap<>();
    private JButton refreshButton;

    public SalesPanel() {
        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Sales Records", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] { "Sale ID", "Medicine Name", "Quantity", "Total Price", "Sale Date" }, 0);
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add the refresh button at the bottom
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshSalesTable());  // Add action listener to refresh the table
        add(refreshButton, BorderLayout.SOUTH);

        fetchMedicines(); // Load ID->Name map
        fetchSales();     // Load sales data
    }

    private void fetchMedicines() {
        try {
            URL url = new URL("http://localhost:8080/api/medicines");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String response = br.lines().reduce("", (a, b) -> a + b);
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    medicineIdToName.put(obj.getInt("id"), obj.getString("name"));
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load medicines.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchSales() {
        try {
            URL url = new URL("http://localhost:8080/api/sales");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String response = br.lines().reduce("", (a, b) -> a + b);
                JSONArray array = new JSONArray(response);

                // Clear existing data before loading new data
                tableModel.setRowCount(0);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject sale = array.getJSONObject(i);
                    int id = sale.getInt("id");
                    int medicineId = sale.getInt("medicineId"); // Use correct field
                    int quantity = sale.getInt("quantity");
                    double totalPrice = sale.getDouble("totalPrice");
                    String saleDate = sale.getString("saleDate");

                    String medName = medicineIdToName.getOrDefault(medicineId, "Unknown");

                    tableModel.addRow(new Object[] {
                            id, medName, quantity, String.format("$%.2f", totalPrice), saleDate
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load sales data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // New method to refresh the sales table
    public void refreshSalesTable() {
        // Clear the existing table data
        tableModel.setRowCount(0);
        // Fetch the updated sales data
        fetchSales();
    }

    // Add a method that can be called after a sale is made to update the table
    public void handleSaleAdded() {
        refreshSalesTable();  // Refresh the sales table with new data
    }
}
