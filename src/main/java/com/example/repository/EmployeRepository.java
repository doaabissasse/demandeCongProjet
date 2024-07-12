package com.example.repository;


import com.example.resources.Employe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeRepository extends MongoRepository<Employe, String> {
    Optional<Employe> findByEmail(String email);

    boolean existsByEmail(String email);
}