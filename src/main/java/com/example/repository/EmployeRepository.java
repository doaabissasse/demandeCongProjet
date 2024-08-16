package com.example.repository;


import com.example.resources.Employe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends MongoRepository<Employe, String> {
    Optional<Employe> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Employe> findByUsername(String username);

    long countByRole(String role);

    List<Employe> findByRole(String role);

    List<Employe> findByRoleAndNomContainingIgnoreCaseOrRoleAndPrenomContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
            String role1, String nom, String role2, String prenom, String role3, String email);
}