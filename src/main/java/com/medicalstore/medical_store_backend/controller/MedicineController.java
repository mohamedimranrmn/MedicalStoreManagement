package com.medicalstore.medical_store_backend.controller;

import com.medicalstore.medical_store_backend.model.Medicine;
import com.medicalstore.medical_store_backend.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    // Get all medicines
    @GetMapping
    public List<Medicine> getAllMedicines() {
        return medicineService.getAllMedicines();
    }

    // Add new medicine
    @PostMapping
    public ResponseEntity<Medicine> addMedicine(@RequestBody Medicine medicine) {
        Medicine saved = medicineService.addMedicine(medicine);
        return ResponseEntity.ok(saved);
    }

    // Purchase existing medicine
    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseMedicine(
            @RequestParam int medicineId,
            @RequestParam int quantity) {
        medicineService.purchaseMedicine(medicineId, quantity);
        return ResponseEntity.ok("Purchase successful!");
    }

    // Update medicine
    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable int id, @RequestBody Medicine updated) {
        Medicine result = medicineService.updateMedicine(id, updated);
        return ResponseEntity.ok(result);
    }

    // Delete medicine
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicine(@PathVariable int id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.ok("Medicine deleted successfully.");
    }
}
