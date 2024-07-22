package com.example.resources;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "salarier")
public class Employe {
    @Id
    private ObjectId id;
    private String username;
    private String nom;
    private String prenom;
    private String CIN;
    private Date date_naissance;
    private String sexe;
    private Adresse adresse;
    private String email;
    private String telephone;
    private String poste;
    private String departement;
    private Date date_embauche;
    private int salaire;
    private String mot_de_passe;
    private String role; // Ajouter le rôle ici
    private SoldeConges solde_conges;

    // Constructeurs, getters, et setters
    public Employe(String nom, String prenom, String email, String mot_de_passe, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mot_de_passe = mot_de_passe;
        this.role = role; // Initialiser le rôle
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }



    // Getters et setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }


    public String getCIN() {
        return CIN;
    }

    public void setCIN(String CIN) {
        this.CIN = CIN;
    }

    public Date getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(Date date_naissance) {
        this.date_naissance = date_naissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public Date getDate_embauche() {
        return date_embauche;
    }

    public void setDate_embauche(Date date_embauche) {
        this.date_embauche = date_embauche;
    }

    public int getSalaire() {
        return salaire;
    }

    public void setSalaire(int salaire) {
        this.salaire = salaire;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    public SoldeConges getSolde_conges() {
        return solde_conges;
    }

    public void setSolde_conges(SoldeConges solde_conges) {
        this.solde_conges = solde_conges;
    }

    // Classes Adresse, Conges, et SoldeConges
    public static class Adresse {
        private String rue;
        private String ville;
        private String code_postal;
        private String pays;

        // Getters et setters
        public String getRue() {
            return rue;
        }

        public void setRue(String rue) {
            this.rue = rue;
        }

        public String getVille() {
            return ville;
        }

        public void setVille(String ville) {
            this.ville = ville;
        }

        public String getCode_postal() {
            return code_postal;
        }

        public void setCode_postal(String code_postal) {
            this.code_postal = code_postal;
        }

        public String getPays() {
            return pays;
        }

        public void setPays(String pays) {
            this.pays = pays;
        }
    }

    public static class SoldeConges {
        private int payes;
        private int non_payes;
        private int maladie;

        // Getters et setters
        public int getPayes() {
            return payes;
        }

        public void setPayes(int payes) {
            this.payes = payes;
        }

        public int getNon_payes() {
            return non_payes;
        }

        public void setNon_payes(int non_payes) {
            this.non_payes = non_payes;
        }

        public int getMaladie() {
            return maladie;
        }

        public void setMaladie(int maladie) {
            this.maladie = maladie;
        }
    }
}
