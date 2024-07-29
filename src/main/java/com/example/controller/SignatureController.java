package com.example.controller;

import com.example.resources.Signature;
import com.example.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/signatures")
public class SignatureController {
    @Autowired
    private SignatureService signatureService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSignature(@RequestParam("userId") String userId, @RequestParam("file") MultipartFile file) {
        try {
            // Enregistrez le fichier dans un répertoire et obtenez l'URL
            String folder = "signatures/";
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folder + userId + "_" + file.getOriginalFilename());
            Files.write(path, bytes);
            String signatureUrl = path.toString();

            // Enregistrez les informations de la signature dans MongoDB
            Signature signature = new Signature();
            signature.setUserId(userId);
            signature.setSignatureUrl(signatureUrl);
            signatureService.saveSignature(signature);

            return ResponseEntity.ok("Signature uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading signature.");
        }
    }
}
