package com.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        // Créez une instance de BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Hash de la base de données
        String storedHash = "$2a$10$aMcMyo8jUjBd71uIjKcH3OXWr8FtqPe2vhhoDm.2UlInYyqZ9uCZS";

        // Mot de passe que vous voulez vérifier
        String rawPassword = "doaabissasse2002";

        // Vérifiez si le mot de passe correspond au hash
        boolean matches = encoder.matches(rawPassword, storedHash);

        // Affichez le résultat
        if (matches) {
            System.out.println("Le mot de passe est valide !");
        } else {
            System.out.println("Le mot de passe est invalide.");
        }
    }
}
