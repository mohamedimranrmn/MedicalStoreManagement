package com.medicalstore.medical_store_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/")
    public String home() {
        return "home"; // home.jsp
    }

    @GetMapping("/medicines")
    public String medicines() {
        return "medicines"; // medicines.jsp
    }

    @GetMapping("/sales")
    public String sales() {
        return "sales"; // sales.jsp
    }
}
