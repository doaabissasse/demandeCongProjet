package com.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        // Créez une instance de BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Mot de passe brut que vous voulez hacher
        String rawPassword = "aymenloudiy1995";

        // Hachez le mot de passe
        String hashedPassword = encoder.encode(rawPassword);

        // Affichez le mot de passe haché
        System.out.println("Mot de passe haché : " + hashedPassword);
        boolean matches = encoder.matches(rawPassword, hashedPassword);

        // Affichez le résultat
        if (matches) {
            System.out.println("Le mot de passe est valide !");
        } else {
            System.out.println("Le mot de passe est invalide.");
        }
    }
}

