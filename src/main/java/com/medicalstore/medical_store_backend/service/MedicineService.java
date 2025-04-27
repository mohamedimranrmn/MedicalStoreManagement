package com.medicalstore.medical_store_backend.service;

import com.medicalstore.medical_store_backend.model.Medicine;
import com.medicalstore.medical_store_backend.model.Sale;
import com.medicalstore.medical_store_backend.repository.MedicineRepository;
import com.medicalstore.medical_store_backend.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private SaleRepository saleRepository;

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine addMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public void purchaseMedicine(int medicineId, int quantity) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        if (medicine.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        // Update stock
        medicine.setStock(medicine.getStock() - quantity);
        medicineRepository.save(medicine);

        // Record sale
        Sale sale = new Sale();
        sale.setMedicineId(medicineId);
        sale.setQuantity(quantity);
        sale.setTotalPrice(medicine.getPrice() * quantity);
        saleRepository.save(sale);
    }

    public Medicine updateMedicine(int id, Medicine updated) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        medicine.setName(updated.getName());
        medicine.setDescription(updated.getDescription());
        medicine.setPrice(updated.getPrice());
        medicine.setStock(updated.getStock());

        return medicineRepository.save(medicine);
    }

    public void deleteMedicine(int id) {
        medicineRepository.deleteById(id);
    }
}
