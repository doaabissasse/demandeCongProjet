package com.example.controller;

import com.example.repository.EmployeRepository;
import com.example.resources.Employe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeController {

    @Autowired
    private EmployeRepository employeRepository;

    @GetMapping("/salarier")
    public List<Employe> getAllEmployes() {
        return employeRepository.findAll();
    }

    @GetMapping("/salarier/{email}")
    public Employe getEmployeByEmail(@PathVariable String email) {
        return employeRepository.findByEmail(email).orElse(null);
    }
}
