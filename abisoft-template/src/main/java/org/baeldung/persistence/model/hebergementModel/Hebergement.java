package org.baeldung.persistence.model.hebergementModel;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;
import org.baeldung.persistence.model.hebergementModelTarifType.HebergementTarifType;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.persistence.model.hebergementModel.ValidationStatus;
import org.baeldung.persistence.model.User;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "hebergement")
public class Hebergement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String adresse;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int capacite;
    
    private LocalDate dateDebut;
    
    private LocalDate dateFin;

    private Double prix;

    private Double nbEtoiles;

    private Boolean actif;

    private String codePostal;

    private Boolean populaire;

    private String type;
    
    @ManyToOne
    private Ville ville;
    
    @ManyToOne
    private Pay pays;
    
    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    private User proprietaire;
    
    @Enumerated(EnumType.STRING)
    private HebergementTarifType tarifType;
    
    @Enumerated(EnumType.STRING)
    private HebergementType hebergementType;

    // URL d'une photo unique
    @Column(length = 1000)
    private String photoUrl;

    // Images supplémentaires
    @OneToMany(mappedBy = "hebergement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HebergementImage> imagesSupplementaires = new ArrayList<>();
    
    // Statut de validation de l'hébergement
    @Enumerated(EnumType.STRING)
    private ValidationStatus validationStatus = ValidationStatus.EN_ATTENTE;

    // --- Gestion des chambres associées ---
    @OneToMany(mappedBy = "hebergement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chambre> chambres = new ArrayList<>();
    
    // Commentaire de refus (si applicable)
    private String commentaireRefus;

    // Méthode utilitaire pour obtenir une URL d'image par défaut si photoUrl est null
    public String getPhotoUrlWithDefault() {
        if (photoUrl == null || photoUrl.isEmpty()) {
            // Générer un numéro aléatoire entre 1 et 5 pour varier les images
            int imageNumber = (int) (Math.random() * 5) + 1;
            
            // Utiliser directement le type pour construire le nom de l'image
            String typeToUse;
            
            // Déterminer le type d'hébergement
            if (type != null && !type.isEmpty()) {
                typeToUse = type.toLowerCase();
                
                // Normaliser certains types pour correspondre aux noms de fichiers
                if (typeToUse.contains("appartement") || typeToUse.contains("apartements")) {
                    typeToUse = "appartement";
                } else if (typeToUse.contains("maison_hote") || typeToUse.contains("maison")) {
                    typeToUse = "maison_hote";
                } else if (typeToUse.contains("riad")) {
                    typeToUse = "riad";
                } else if (typeToUse.contains("hotel")) {
                    typeToUse = "hotel";
                }
            } else if (hebergementType != null) {
                typeToUse = hebergementType.name().toLowerCase();
            } else {
                // Type par défaut si aucun type n'est spécifié
                typeToUse = "hotel";
            }
            
            // Construire le chemin de l'image
            return "/assets/img/" + typeToUse + "_" + imageNumber + ".jpg";
        }
        return photoUrl;
    }
    
    // Getter et Setter pour le propriétaire
    public User getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(User proprietaire) {
        this.proprietaire = proprietaire;
    }
    
    // Getters et Setters pour validationStatus
    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }
    
    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    // Getters et Setters pour commentaireRefus
    public String getCommentaireRefus() {
        return commentaireRefus;
    }
    
    public void setCommentaireRefus(String commentaireRefus) {
        this.commentaireRefus = commentaireRefus;
    }
    
    // Getters et setters pour les autres propriétés
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCapacite() {
        return capacite;
    }
    
    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public Double getPrix() {
        return prix;
    }
    
    public void setPrix(Double prix) {
        this.prix = prix;
    }
    
    public Double getNbEtoiles() {
        return nbEtoiles;
    }
    
    public void setNbEtoiles(Double nbEtoiles) {
        this.nbEtoiles = nbEtoiles;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
    
    public String getCodePostal() {
        return codePostal;
    }
    
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    
    public Boolean getPopulaire() {
        return populaire;
    }
    
    public void setPopulaire(Boolean populaire) {
        this.populaire = populaire;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Ville getVille() {
        return ville;
    }
    
    public void setVille(Ville ville) {
        this.ville = ville;
    }
    
    public Pay getPays() {
        return pays;
    }
    
    public void setPays(Pay pays) {
        this.pays = pays;
    }
    
    public HebergementTarifType getTarifType() {
        return tarifType;
    }
    
    public void setTarifType(HebergementTarifType tarifType) {
        this.tarifType = tarifType;
    }
    
    public HebergementType getHebergementType() {
        return hebergementType;
    }
    
    public void setHebergementType(HebergementType hebergementType) {
        this.hebergementType = hebergementType;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<HebergementImage> getImagesSupplementaires() {
        return imagesSupplementaires;
    }

    public void setImagesSupplementaires(List<HebergementImage> imagesSupplementaires) {
        this.imagesSupplementaires = imagesSupplementaires;
    }

    public List<Chambre> getChambres() {
        return chambres;
    }
    
    public void setChambres(List<Chambre> chambres) {
        this.chambres = chambres;
    }
    
}

