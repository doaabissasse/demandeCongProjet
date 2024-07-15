package com.example.controller;

import com.example.repository.EmployeRepository;
import com.example.resources.Employe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
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
}
