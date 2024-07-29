package com.example.repository;


import com.example.resources.Signature;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SignatureRepository extends MongoRepository<Signature, String> {
    Optional<Signature> findByUserId(String userId);
}

