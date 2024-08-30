package com.example.config.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;  // Clé secrète utilisée pour signer les jetons JWT

    @Value("${jwt.jwtExpirationMs}")
    private int jwtExpirationMs;  // Durée de validité des jetons JWT en millisecondes

    /**
     * Génère un jeton JWT pour l'utilisateur authentifié.
     *
     * @param authentication l'objet Authentication contenant les détails de l'utilisateur.
     * @return le jeton JWT généré sous forme de chaîne de caractères.
     */
    public String generateJwtToken(Authentication authentication) {
        // Récupère les détails de l'utilisateur à partir de l'objet Authentication
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Construit le jeton JWT en y incluant le nom d'utilisateur, le rôle, la date de création,
        // la date d'expiration et en le signant avec l'algorithme HS512 et la clé secrète.
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())  // Définit le sujet (nom d'utilisateur)
                .claim("role", userPrincipal.getAuthorities().iterator().next().getAuthority())  // Ajoute le rôle de l'utilisateur dans les claims
                .setIssuedAt(new Date())  // Définit la date de création du jeton
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))  // Définit la date d'expiration du jeton
                .signWith(SignatureAlgorithm.HS512, jwtSecret)  // Signe le jeton avec la clé secrète
                .compact();  // Compacte le jeton en une chaîne de caractères
    }

    /**
     * Extrait le nom d'utilisateur (subject) du jeton JWT.
     *
     * @param token le jeton JWT sous forme de chaîne de caractères.
     * @return le nom d'utilisateur extrait du jeton ou null en cas d'échec de la validation.
     */
    public String getUserNameFromJwtToken(String token) {
        try {
            // Analyse le jeton JWT et retourne le sujet (nom d'utilisateur) s'il est valide
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException e) {
            // En cas d'exception lors de l'analyse du jeton, retourne null
            return null;
        }
    }

    /**
     * Valide le jeton JWT.
     *
     * @param authToken le jeton JWT à valider.
     * @return true si le jeton est valide, false sinon.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Analyse le jeton JWT avec la clé secrète pour vérifier sa validité
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            // En cas d'exception lors de l'analyse du jeton, retourne false
            return false;
        }
    }

    /**
     * Extrait le nom d'utilisateur du jeton JWT en utilisant la méthode extractClaim.
     *
     * @param token le jeton JWT sous forme de chaîne de caractères.
     * @return le nom d'utilisateur extrait du jeton.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du jeton JWT.
     *
     * @param token le jeton JWT sous forme de chaîne de caractères.
     * @return la date d'expiration du jeton.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait une information (claim) spécifique du jeton JWT en utilisant un résolveur de claims.
     *
     * @param token le jeton JWT sous forme de chaîne de caractères.
     * @param claimsResolver une fonction lambda pour résoudre le claim souhaité.
     * @param <T> le type du claim à extraire.
     * @return le claim extrait du jeton.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du jeton JWT.
     *
     * @param token le jeton JWT sous forme de chaîne de caractères.
     * @return les claims extraits du jeton.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    /**
     * Vérifie si le jeton JWT est expiré.
     *
     * @param token le jeton JWT sous forme de chaîne de caractères.
     * @return true si le jeton est expiré, false sinon.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}


