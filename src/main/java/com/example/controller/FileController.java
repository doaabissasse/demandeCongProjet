package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController // Indique que cette classe est un contrôleur REST, ce qui signifie que les méthodes de cette classe vont gérer les requêtes HTTP.
@RequestMapping("/api/files") // Définit le chemin de base pour les routes de ce contrôleur. Toutes les routes commencent par /api/files.
public class FileController {

    // Injecte la valeur de la propriété 'file.upload-dir' définie dans les fichiers de configuration de l'application.
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Gère les requêtes HTTP POST pour l'upload de fichiers.
     *
     * @param file Le fichier téléchargé par l'utilisateur via la requête.
     * @return ResponseEntity contenant le nom du fichier sauvegardé ou un message d'erreur en cas d'échec.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // Génère un nom de fichier unique en ajoutant un UUID au nom d'origine pour éviter les collisions de noms.
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        // Construit le chemin complet où le fichier sera sauvegardé.
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            // Copie le contenu du fichier téléchargé vers l'emplacement de sauvegarde.
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // Retourne une réponse HTTP 200 avec le nom du fichier en cas de succès.
            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            // En cas d'erreur pendant l'upload, retourne une réponse HTTP 500 avec un message d'erreur.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    /**
     * Gère les requêtes HTTP GET pour le téléchargement de fichiers.
     *
     * @param fileName Le nom du fichier à télécharger, fourni dans l'URL.
     * @return ResponseEntity contenant le fichier en tant que ressource ou un code d'erreur en cas d'échec.
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        // Construit le chemin complet où le fichier est situé.
        Path filePath = Paths.get(uploadDir, fileName);
        Resource resource;

        try {
            // Tente de créer une ressource à partir du chemin du fichier.
            resource = new UrlResource(filePath.toUri());
            // Vérifie si le fichier existe et est lisible.
            if (resource.exists() || resource.isReadable()) {
                // Si le fichier est valide, retourne une réponse HTTP 200 avec le fichier en tant que pièce jointe.
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                // Si le fichier n'existe pas ou n'est pas lisible, retourne une réponse HTTP 404 (Non trouvé).
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            // En cas d'erreur lors de la création de la ressource, retourne une réponse HTTP 500.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

