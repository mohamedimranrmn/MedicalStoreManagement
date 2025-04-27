<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Medicines</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f8ff; }
        .container { width: 90%; margin: 20px auto; }
        h2 { text-align: center; background: #2196f3; color: white; padding: 10px; border-radius: 8px; }
        table { width: 100%; margin-top: 20px; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; }
        th, td { padding: 10px; text-align: center; border-bottom: 1px solid #ddd; }
        th { background: #1976d2; color: white; }
        input[type="text"], input[type="number"] { width: 90%; padding: 8px; margin: 5px 0; }
        button { padding: 10px 20px; margin: 10px 5px; border: none; color: white; cursor: pointer; font-size: 16px; border-radius: 6px; }
        .btn-add { background: #4caf50; }
        .btn-update { background: #ffc107; color: black; }
        .btn-delete { background: #f44336; }
        .btn-refresh { background: #2196f3; }
        .form-container { margin-top: 30px; display: flex; flex-direction: column; align-items: center; }
    </style>
</head>
<body>
<div class="container">
    <h2>Manage Medicines</h2>

    <table id="medicineTable">
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Price</th>
            <th>Stock</th>
        </tr>
        </thead>
        <tbody></tbody>
    </table>

    <div class="form-container">
        <input type="hidden" id="medicineId" />
        <input type="text" id="medicineName" placeholder="Name" />
        <input type="text" id="medicineDescription" placeholder="Description" />
        <input type="number" id="medicinePrice" placeholder="Price" />
        <input type="number" id="medicineStock" placeholder="Stock" />

        <div>
            <button class="btn-add" onclick="addMedicine()"><i class="fas fa-plus-circle"></i> Add</button>
            <button class="btn-update" onclick="updateMedicine()"><i class="fas fa-sync-alt"></i> Update</button>
            <button class="btn-delete" onclick="deleteMedicine()"><i class="fas fa-trash"></i> Delete</button>
            <button class="btn-refresh" onclick="fetchMedicines()"><i class="fas fa-sync"></i> Refresh</button>
        </div>
    </div>
</div>

<script>
    const API_URL = "/api/medicines";

    function fetchMedicines() {
        fetch(API_URL)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch medicines.');
                }
                return response.json();
            })
            .then(data => {
                const tbody = document.getElementById("medicineTable").querySelector("tbody");
                tbody.innerHTML = "";

                if (data.length === 0) {
                    const row = tbody.insertRow();
                    row.innerHTML = `<td colspan="5">No medicines found.</td>`;
                    return;
                }

                data.forEach(med => {
                    const row = tbody.insertRow();
                    row.onclick = () => selectMedicine(med);
                    row.innerHTML = `
                        <td>${med.id}</td>
                        <td>${med.name}</td>
                        <td>${med.description}</td>
                        <td>${med.price}</td>
                        <td>${med.stock}</td>
                    `;
                });
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error loading medicines data.');
            });
    }

    function selectMedicine(med) {
        document.getElementById("medicineId").value = med.id;
        document.getElementById("medicineName").value = med.name;
        document.getElementById("medicineDescription").value = med.description;
        document.getElementById("medicinePrice").value = med.price;
        document.getElementById("medicineStock").value = med.stock;
    }

    function addMedicine() {
        const newMed = {
            name: document.getElementById("medicineName").value.trim(),
            description: document.getElementById("medicineDescription").value.trim(),
            price: parseFloat(document.getElementById("medicinePrice").value),
            stock: parseInt(document.getElementById("medicineStock").value)
        };

        if (!newMed.name || isNaN(newMed.price) || isNaN(newMed.stock)) {
            alert('Please fill in all fields correctly.');
            return;
        }

        fetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(newMed)
        })
            .then(response => {
                if (response.ok) {
                    fetchMedicines();
                    clearForm();
                } else {
                    alert('Failed to add medicine.');
                }
            });
    }

    function updateMedicine() {
        const id = document.getElementById("medicineId").value;
        if (!id) return alert("Select a medicine to update!");

        const updatedMed = {
            name: document.getElementById("medicineName").value.trim(),
            description: document.getElementById("medicineDescription").value.trim(),
            price: parseFloat(document.getElementById("medicinePrice").value),
            stock: parseInt(document.getElementById("medicineStock").value)
        };

        if (!updatedMed.name || isNaN(updatedMed.price) || isNaN(updatedMed.stock)) {
            alert('Please fill in all fields correctly.');
            return;
        }

        fetch(`${API_URL}/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedMed)
        })
            .then(response => {
                if (response.ok) {
                    fetchMedicines();
                    clearForm();
                } else {
                    alert('Failed to update medicine.');
                }
            });
    }

    function deleteMedicine() {
        const id = document.getElementById("medicineId").value;
        if (!id) return alert("Select a medicine to delete!");

        fetch(`${API_URL}/${id}`, { method: "DELETE" })
            .then(response => {
                if (response.ok) {
                    fetchMedicines();
                    clearForm();
                } else {
                    alert('Failed to delete medicine.');
                }
            });
    }

    function clearForm() {
        document.getElementById("medicineId").value = "";
        document.getElementById("medicineName").value = "";
        document.getElementById("medicineDescription").value = "";
        document.getElementById("medicinePrice").value = "";
        document.getElementById("medicineStock").value = "";
    }

    window.onload = fetchMedicines;
</script>
</body>
</html>
