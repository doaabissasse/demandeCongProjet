package com.example.controller;

import com.example.payload.request.LoginRequest;
import com.example.payload.response.JwtResponse;
import com.example.config.jwt.JwtUtils;
import com.example.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@RestController // Indique que cette classe est un contrôleur REST, ce qui signifie que les méthodes de cette classe vont gérer les requêtes HTTP.
@RequestMapping("/api/auth") // Spécifie le chemin de base pour toutes les requêtes HTTP traitées par ce contrôleur.
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager; // Injecte automatiquement le gestionnaire d'authentification utilisé pour authentifier les utilisateurs.

    @Autowired
    JwtUtils jwtUtils; // Injecte automatiquement un utilitaire pour gérer les tokens JWT.

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Crée un logger pour enregistrer des messages d'information ou d'erreur.

    /**
     * Authentifie un utilisateur sur la base de ses informations d'identification et génère un token JWT.
     *
     * @param loginRequest Objet contenant les informations d'identification de l'utilisateur (nom d'utilisateur et mot de passe).
     * @return ResponseEntity contenant le token JWT si l'authentification réussit, sinon une réponse d'erreur.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Tente d'authentifier l'utilisateur avec le nom d'utilisateur et le mot de passe fournis.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Enregistre un message indiquant que l'authentification a réussi.
            logger.info("Authentification réussie pour username: " + loginRequest.getUsername());

            // Définit l'objet Authentication dans le contexte de sécurité pour l'utilisateur authentifié.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Génére un token JWT pour l'utilisateur authentifié.
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Récupère les détails de l'utilisateur actuellement authentifié.
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Récupère les rôles de l'utilisateur sous forme de chaîne de caractères.
            String role = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.joining(","));

            // Retourne une réponse contenant le token JWT et les informations de l'utilisateur.
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId().toString(),
                    userDetails.getUsername(),
                    role));
        } catch (Exception e) {
            // Enregistre un message d'erreur si l'authentification échoue.
            logger.error("Authentification échouée: " + e.getMessage());
            // Retourne une réponse HTTP 401 (non autorisé) si l'authentification échoue.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
