package com.example.config.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Le composant `AuthEntryPointJwt` est utilisé pour gérer les erreurs d'authentification dans une application Spring Boot sécurisée.
 * Il implémente l'interface `AuthenticationEntryPoint` de Spring Security, qui est appelée chaque fois qu'une demande non authentifiée
 * est faite à une ressource sécurisée.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    /**
     * Cette méthode est invoquée chaque fois qu'un utilisateur non authentifié tente d'accéder à une ressource sécurisée
     * et échoue à s'authentifier. Elle envoie une réponse d'erreur HTTP 401 (Unauthorized) au client.
     *
     * @param request       l'objet `HttpServletRequest` qui contient les informations de la requête HTTP.
     * @param response      l'objet `HttpServletResponse` qui est utilisé pour envoyer la réponse HTTP au client.
     * @param authException l'exception qui est levée lorsque l'authentification échoue.
     * @throws IOException si une erreur d'entrée/sortie survient lors de l'envoi de la réponse.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        // Envoie une réponse HTTP 401 avec le message "Error: Unauthorized" pour indiquer que l'accès est refusé.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}

