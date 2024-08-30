package com.example.config.jwt;

import com.example.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Le filtre `AuthTokenFilter` est un filtre personnalisé qui intercepte chaque requête HTTP
 * pour valider le jeton JWT et authentifier l'utilisateur associé.
 * Ce filtre s'exécute une seule fois par requête (grâce à l'héritage de `OncePerRequestFilter`).
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;  // Utilitaire pour la gestion des jetons JWT

    @Autowired
    private CustomUserDetailsService userDetailsService;  // Service pour charger les détails de l'utilisateur

    /**
     * Cette méthode est exécutée pour chaque requête HTTP interceptée par le filtre.
     * Elle extrait le jeton JWT, le valide, et authentifie l'utilisateur si le jeton est valide.
     *
     * @param request  l'objet `HttpServletRequest` contenant les informations de la requête.
     * @param response l'objet `HttpServletResponse` utilisé pour envoyer la réponse.
     * @param chain    l'objet `FilterChain` permettant de passer au filtre suivant dans la chaîne.
     * @throws IOException      si une erreur d'entrée/sortie se produit.
     * @throws ServletException si une erreur générale se produit dans la chaîne de filtres.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // Extrait le jeton JWT de la requête HTTP
            String jwt = parseJwt(request);

            // Si le jeton JWT est présent et valide
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Extrait le nom d'utilisateur à partir du jeton JWT
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                if (username != null) {
                    // Charge les détails de l'utilisateur associé au nom d'utilisateur
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Crée un objet d'authentification basé sur les détails de l'utilisateur
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // Définit l'objet d'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException e) {
            // Capture et affiche toute exception liée au jeton JWT
            System.out.println("JWT exception: " + e.getMessage());
        }

        // Passe la requête et la réponse au filtre suivant dans la chaîne
        chain.doFilter(request, response);
    }

    /**
     * Méthode privée pour extraire le jeton JWT de l'en-tête HTTP Authorization.
     *
     * @param request l'objet `HttpServletRequest` contenant les informations de la requête.
     * @return le jeton JWT extrait ou null s'il n'est pas présent ou ne commence pas par "Bearer ".
     */
    private String parseJwt(HttpServletRequest request) {
        // Récupère l'en-tête Authorization de la requête
        String headerAuth = request.getHeader("Authorization");

        // Vérifie que l'en-tête est présent et commence par "Bearer "
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            // Retourne le jeton sans le préfixe "Bearer "
            return headerAuth.substring(7);
        }

        // Retourne null si l'en-tête Authorization est absent ou incorrect
        return null;
    }
}
