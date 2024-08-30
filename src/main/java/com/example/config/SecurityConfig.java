package com.example.config;

import com.example.config.jwt.AuthEntryPointJwt;
import com.example.config.jwt.AuthTokenFilter;
import com.example.service.CustomUserDetailsService;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Injection de dépendance pour le service de gestion des détails utilisateur personnalisé
    @Autowired
    CustomUserDetailsService userDetailsService;

    // Injection de dépendance pour la gestion des points d'entrée non autorisés
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Définit un filtre d'authentification JWT personnalisé à utiliser dans la chaîne de filtres de sécurité
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Définit un fournisseur d'authentification basé sur DAO pour authentifier les utilisateurs en utilisant les informations d'identification et les détails utilisateur personnalisés
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); // Spécifie l'encodeur de mot de passe à utiliser
        return authProvider;
    }

    // Définit un gestionnaire d'authentification pour l'application, utilisé par Spring Security
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Définit un encodeur de mot de passe utilisant BCrypt, recommandé pour son niveau de sécurité
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure les règles CORS pour permettre les requêtes provenant de certaines origines (comme localhost:3000)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Spécifie les origines autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Spécifie les méthodes HTTP autorisées
        configuration.setAllowedHeaders(Arrays.asList("*")); // Autorise tous les en-têtes
        configuration.setAllowCredentials(true); // Autorise l'envoi de cookies et d'informations d'authentification

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applique cette configuration à tous les chemins d'URL
        return source;
    }

    // Configure la chaîne de filtres de sécurité pour Spring Security
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactive la protection CSRF (Cross-Site Request Forgery)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Active et configure CORS
                .exceptionHandling(e -> e
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(403)) // Gestion des accès refusés
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))) // Gestion des tentatives d'accès non autorisées
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Utilise une politique de session sans état
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // Définition des règles de sécurité pour chaque chemin d'URL
                        .requestMatchers("/api/absences/{id}/accept").permitAll()
                        .requestMatchers("/api/absences/{id}/reject").permitAll()
                        .requestMatchers("/api/absences/pending").permitAll()
                        .requestMatchers("/api/leave-requests/{id}/admin-approve").permitAll()
                        .requestMatchers("/api/absences/{id}/admin-approve").permitAll()
                        .requestMatchers("/api/files/upload").permitAll()
                        .requestMatchers("/api/files/download/{fileName:.+}").permitAll()
                        .requestMatchers("/api/absences").permitAll()
                        .requestMatchers("/api/employees").permitAll()
                        .requestMatchers("/api/leave-requests/employee/{employeeId}").permitAll()
                        .requestMatchers("/api/absences/employee/{employeeId}/absences").permitAll()
                        .requestMatchers("/api/absences/{id}").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/employee/{username}/leave-requests").permitAll()
                        .requestMatchers("/api/absences/{id}").permitAll()
                        .requestMatchers("/api/absences/employee/{employeeId}").permitAll()
                        .requestMatchers("/api/pdf/generate").permitAll()
                        .requestMatchers("/api/employees/{id}").permitAll()
                        .requestMatchers("/api/signout").permitAll()
                        .requestMatchers("/api/{id}").permitAll()
                        .requestMatchers("/api/dashboard/employee-count").permitAll()
                        .requestMatchers("/api/dashboard/leave-request-count").permitAll()
                        .requestMatchers("/api/events").permitAll()
                        .requestMatchers("/api/salarier/**").permitAll()
                        .requestMatchers("/api/leave-requests").permitAll()
                        .requestMatchers("/api/leave-requests/user/{id}").permitAll()
                        .requestMatchers("/api/leave-requests/{id}/reject").permitAll()
                        .requestMatchers("/api/leave-requests/{id}/approve").permitAll()
                        .requestMatchers("/api/leave-request").permitAll()
                        .requestMatchers("/api/test-cors").permitAll()
                        .requestMatchers("/api/leave-requests/{id}/pdf").permitAll()
                        .requestMatchers("/api/leave-requests/{id}/refuse").permitAll()
                        .requestMatchers("/api/leave-requests/{id}/accept").permitAll()
                        .requestMatchers("/api/me").authenticated() // Assure que seul les utilisateurs authentifiés peuvent accéder à /api/me
                        .anyRequest().authenticated() // Toute autre requête doit être authentifiée
                )
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Ajoute le filtre JWT avant le filtre d'authentification par défaut

        return http.build(); // Construit l'objet SecurityFilterChain avec la configuration spécifiée
    }
}

