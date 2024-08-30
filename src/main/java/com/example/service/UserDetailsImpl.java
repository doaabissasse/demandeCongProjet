package com.example.service;

import com.example.resources.Employe;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implémentation personnalisée de UserDetails pour Spring Security.
 * Fournit les détails de l'utilisateur, y compris les informations de connexion et les autorisations.
 */
public class UserDetailsImpl implements UserDetails {
    private String id; // Identifiant unique de l'utilisateur
    private String username; // Nom d'utilisateur (souvent l'email)
    private String password; // Mot de passe de l'utilisateur
    private GrantedAuthority authority; // Autorité ou rôle accordé à l'utilisateur

    /**
     * Constructeur pour initialiser les détails de l'utilisateur.
     * @param id Identifiant unique de l'utilisateur.
     * @param username Nom d'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     * @param authority Autorité ou rôle accordé à l'utilisateur.
     */
    public UserDetailsImpl(String id, String username, String password, GrantedAuthority authority) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authority = authority;
    }

    /**
     * Crée une instance de UserDetailsImpl à partir d'un objet Employe.
     * @param employe L'objet Employe contenant les informations de l'utilisateur.
     * @return Une nouvelle instance de UserDetailsImpl.
     */
    public static UserDetailsImpl build(Employe employe) {
        // Crée une autorité basée sur le rôle de l'employé
        GrantedAuthority authority = new SimpleGrantedAuthority(employe.getRole());

        // Retourne une nouvelle instance de UserDetailsImpl avec les informations de l'employé
        return new UserDetailsImpl(
                employe.getId(),
                employe.getUsername(),
                employe.getMot_de_passe(),
                authority);
    }

    /**
     * Obtient les autorités (rôles) accordées à l'utilisateur.
     * @return Une collection d'autorités.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(authority); // Retourne une collection contenant une seule autorité
    }

    /**
     * Obtient le mot de passe de l'utilisateur.
     * @return Le mot de passe de l'utilisateur.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Obtient le nom d'utilisateur.
     * @return Le nom d'utilisateur.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Vérifie si le compte de l'utilisateur n'est pas expiré.
     * @return Toujours vrai dans cette implémentation (le compte n'est jamais expiré).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Vérifie si le compte de l'utilisateur n'est pas verrouillé.
     * @return Toujours vrai dans cette implémentation (le compte n'est jamais verrouillé).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Vérifie si les informations d'identification de l'utilisateur ne sont pas expirées.
     * @return Toujours vrai dans cette implémentation (les informations d'identification ne sont jamais expirées).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Vérifie si l'utilisateur est activé.
     * @return Toujours vrai dans cette implémentation (l'utilisateur est toujours activé).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Obtient l'identifiant unique de l'utilisateur.
     * @return L'identifiant unique de l'utilisateur.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtient le rôle de l'utilisateur.
     * @return Le rôle accordé à l'utilisateur.
     */
    public String getRole() {
        return authority.getAuthority();
    }
}
