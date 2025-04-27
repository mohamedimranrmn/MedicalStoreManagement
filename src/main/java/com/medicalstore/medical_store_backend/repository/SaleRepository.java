package com.medicalstore.medical_store_backend.repository;

import com.medicalstore.medical_store_backend.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Integer> {}