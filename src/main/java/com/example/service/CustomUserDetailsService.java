package com.example.service;

import com.example.resources.Employe;
import com.example.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service personnalisé pour gérer les détails de l'utilisateur, conforme à l'interface UserDetailsService
 * de Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeRepository employeRepository; // Dépendance pour accéder aux opérations sur les employés

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class); // Logger pour enregistrer les informations de débogage et les erreurs

    /**
     * Charge les détails de l'utilisateur par son nom d'utilisateur.
     * @param username Le nom d'utilisateur de l'utilisateur à rechercher.
     * @return Les détails de l'utilisateur.
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé dans la base de données.
     */
    @Override
    @Transactional // Assure que cette méthode s'exécute dans le contexte d'une transaction.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche l'employé dans la base de données par son nom d'utilisateur.
        Employe employe = employeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employé non trouvé avec le nom d'utilisateur : " + username));

        // Journalise le nom d'utilisateur de l'employé trouvé.
        logger.info("Employé trouvé : " + employe.getUsername());

        // Construit et retourne un objet UserDetails à partir de l'employé trouvé.
        return UserDetailsImpl.build(employe);
    }
}

