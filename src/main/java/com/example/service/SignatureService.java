package com.example.service;

import com.example.resources.Signature;
import com.example.repository.SignatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SignatureService {
    @Autowired
    private SignatureRepository signatureRepository;

    public Optional<Signature> getSignatureByUserId(String userId) {
        return signatureRepository.findByUserId(userId);
    }

    public Signature saveSignature(Signature signature) {
        return signatureRepository.save(signature);
    }
}
