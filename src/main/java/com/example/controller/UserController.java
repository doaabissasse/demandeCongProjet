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
    private EmployeRepository employeRepository; // Dépendance pour accéder aux opérations sur les employés

    /**
     * Méthode pour obtenir les informations de l'utilisateur actuellement authentifié.
     * @return Les détails de l'utilisateur authentifié.
     */
    @GetMapping("/me")
    public Employe getCurrentUser() {
        // Obtenir l'utilisateur authentifié depuis le contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Récupère le nom d'utilisateur de l'utilisateur authentifié

        // Journalise le nom d'utilisateur de l'utilisateur authentifié
        System.out.println("Utilisateur authentifié : " + username);

        // Récupère les détails de l'utilisateur depuis la base de données en utilisant le nom d'utilisateur
        Employe employe = employeRepository.findByUsername(username).orElse(null);
        System.out.println("Employé trouvé : " + employe);
        return employe;
    }

    /**
     * Méthode pour obtenir le nombre d'employés selon leur rôle.
     * @param role Le rôle pour lequel compter les employés.
     * @return Le nombre d'employés ayant ce rôle.
     */
    @GetMapping("/dashboard/employee-count")
    public ResponseEntity<?> getEmployeeCountByRole(@RequestParam String role) {
        long count = employeRepository.countByRole(role); // Compte le nombre d'employés avec le rôle spécifié
        return ResponseEntity.ok(count); // Retourne le compte des employés avec le rôle spécifié
    }

    /**
     * Méthode pour obtenir un employé par son ID.
     * @param id L'ID de l'employé à rechercher.
     * @return Les détails de l'employé si trouvé, sinon une réponse 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employe> getEmployeeById(@PathVariable String id) {
        Optional<Employe> employee = employeRepository.findById(id); // Recherche l'employé par son ID
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get()); // Retourne les détails de l'employé si trouvé
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retourne une réponse 404 si l'employé n'est pas trouvé
        }
    }

    /**
     * Méthode pour obtenir les employés filtrés par rôle et/ou recherche.
     * @param role Le rôle des employés à rechercher (optionnel).
     * @param search Le terme de recherche pour le nom, prénom ou email (optionnel).
     * @return La liste des employés filtrée selon les critères fournis.
     */
    @GetMapping("/employees")
    public ResponseEntity<List<Employe>> getEmployeesByRoleAndSearch(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {

        List<Employe> employees;

        // Filtre les employés en fonction du terme de recherche et du rôle
        if (search != null && !search.isEmpty()) {
            employees = employeRepository.findByRoleAndNomContainingIgnoreCaseOrRoleAndPrenomContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(role, search, role, search, role, search);
        } else if (role != null && !role.isEmpty()) {
            employees = employeRepository.findByRole(role); // Filtre les employés par rôle uniquement
        } else {
            employees = employeRepository.findAll(); // Retourne tous les employés si aucun filtre n'est spécifié
        }

        return ResponseEntity.ok(employees); // Retourne la liste des employés filtrée
    }

    /**
     * Méthode pour obtenir un employé par son ID. Cette méthode est similaire à getEmployeeById,
     * mais elle est explicitement nommée pour la recherche d'employé par ID ou nom.
     * @param id L'ID de l'employé à rechercher.
     * @return Les détails de l'employé si trouvé, sinon une réponse 404.
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employe> getEmployeeByIdName(@PathVariable String id) {
        Optional<Employe> employee = employeRepository.findById(id); // Recherche l'employé par son ID
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); // Retourne les détails de l'employé ou une réponse 404
    }
}
