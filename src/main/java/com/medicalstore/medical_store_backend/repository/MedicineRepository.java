package com.medicalstore.medical_store_backend.repository;

import com.medicalstore.medical_store_backend.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {}