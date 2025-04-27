<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sales Records</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f8ff; }
        .container { width: 90%; margin: 20px auto; }
        h2 { text-align: center; background: #2196f3; color: white; padding: 10px; border-radius: 8px; }
        table { width: 100%; margin-top: 20px; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; }
        th, td { padding: 10px; text-align: center; border-bottom: 1px solid #ddd; }
        th { background: #1976d2; color: white; }
        button { padding: 10px 20px; background: #2196f3; color: white; border: none; margin-top: 20px; font-size: 16px; border-radius: 6px; cursor: pointer; }
    </style>
</head>
<body>
<div class="container">
    <h2>Sales Records</h2>

    <table id="salesTable">
        <thead>
        <tr>
            <th>Sale ID</th>
            <th>Medicine Name</th>
            <th>Quantity</th>
            <th>Total Price</th>
            <th>Sale Date</th>
        </tr>
        </thead>
        <tbody></tbody>
    </table>

    <div style="text-align:center;">
        <button onclick="fetchSales()"><i class="fas fa-sync"></i> Refresh</button>
    </div>
</div>

<script>
    const SALES_API_URL = "/api/sales";

    function fetchSales() {
        fetch(SALES_API_URL)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch sales.');
                }
                return response.json();
            })
            .then(data => {
                const tbody = document.getElementById("salesTable").querySelector("tbody");
                tbody.innerHTML = "";

                if (data.length === 0) {
                    const row = tbody.insertRow();
                    row.innerHTML = `<td colspan="5">No sales records found.</td>`;
                    return;
                }

                data.forEach(sale => {
                    const row = tbody.insertRow();
                    row.innerHTML = `
                        <td>${sale.id}</td>
                        <td>${sale.medicineName}</td>
                        <td>${sale.quantity}</td>
                        <td>$${sale.totalPrice.toFixed(2)}</td>
                        <td>${sale.saleDate}</td>
                    `;
                });
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error loading sales data.');
            });
    }

    window.onload = fetchSales;
</script>
</body>
</html>
