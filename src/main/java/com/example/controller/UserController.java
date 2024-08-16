package com.example.controller;

import com.example.repository.EmployeRepository;
import com.example.resources.Employe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private EmployeRepository employeRepository;

    @GetMapping("/me")
    public Employe getCurrentUser() {
        // Obtenir l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // username de l'utilisateur authentifié

        // Log l'email de l'utilisateur authentifié
        System.out.println("Authenticated user username: " + username);

        // Récupérer les détails de l'utilisateur depuis la base de données
        Employe employe = employeRepository.findByUsername(username).orElse(null);
        System.out.println("Employe found: " + employe);
        return employe;
    }
    @GetMapping("/dashboard/employee-count")
    public ResponseEntity<?> getEmployeeCountByRole( String role) {
        long count = employeRepository.countByRole(role);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Employe> getEmployeeById(@PathVariable String id) {
        Optional<Employe> employee = employeRepository.findById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/employees")
    public ResponseEntity<List<Employe>> getEmployeesByRoleAndSearch(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {

        List<Employe> employees;

        if (search != null && !search.isEmpty()) {
            employees = employeRepository.findByRoleAndNomContainingIgnoreCaseOrRoleAndPrenomContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(role, search, role, search, role, search);
        } else if (role != null && !role.isEmpty()) {
            employees = employeRepository.findByRole(role);
        } else {
            employees = employeRepository.findAll();
        }

        return ResponseEntity.ok(employees);
    }
}
