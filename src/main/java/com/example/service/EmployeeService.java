package com.example.service;

import com.example.resources.Employe;
import com.example.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeRepository employeeRepository;

    public Optional<Employe> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}

