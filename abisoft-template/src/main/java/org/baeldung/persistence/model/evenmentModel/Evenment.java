package org.baeldung.persistence.model.evenmentModel;

import javax.persistence.*;

import org.baeldung.persistence.model.evenmentModelTarifType.EvenementTarifType;
import org.baeldung.persistence.model.evenmentModelType.EvenmentType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "evenements")
public class Evenment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Enumerated(EnumType.STRING)
    private EvenmentType type;

    @Column(length = 1000)
    private String description;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    private String adresse; // Adresse complète
    private String codePostal; // Code postal

    @ManyToOne
    @JoinColumn(name = "ville_id", nullable = false)
    private Ville ville;

    @ManyToOne
    @JoinColumn(name = "pay_id")
    private Pay pays;

    private Double prix;
    private Integer nombreParticipantsMax;
    private String organisateur;
    private String image; // URL ou chemin vers l’image
    private String contactEmail;
    private String contactTelephone;
    private boolean actif = true;

    @Enumerated(EnumType.STRING)
    private EvenementTarifType tarifType; // Type de tarif (Gratuit, Payant, etc.)

    private Double nbEtoiles; // Moyenne des nbEtoiless
    private boolean populaire; // Pour afficher "Les mieux notés"
    
    // Explicit getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public EvenmentType getType() {
        return type;
    }
    
    public void setType(EvenmentType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }
    
    public LocalTime getHeureFin() {
        return heureFin;
    }
    
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getCodePostal() {
        return codePostal;
    }
    
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
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
    
    public Double getPrix() {
        return prix;
    }
    
    public void setPrix(Double prix) {
        this.prix = prix;
    }
    
    public Integer getNombreParticipantsMax() {
        return nombreParticipantsMax;
    }
    
    public void setNombreParticipantsMax(Integer nombreParticipantsMax) {
        this.nombreParticipantsMax = nombreParticipantsMax;
    }
    
    public String getOrganisateur() {
        return organisateur;
    }
    
    public void setOrganisateur(String organisateur) {
        this.organisateur = organisateur;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactTelephone() {
        return contactTelephone;
    }
    
    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    public EvenementTarifType getTarifType() {
        return tarifType;
    }
    
    public void setTarifType(EvenementTarifType tarifType) {
        this.tarifType = tarifType;
    }
    
    public Double getNbEtoiles() {
        return nbEtoiles;
    }
    
    public void setNbEtoiles(Double nbEtoiles) {
        this.nbEtoiles = nbEtoiles;
    }
    
    public boolean isPopulaire() {
        return populaire;
    }
    
    public void setPopulaire(boolean populaire) {
        this.populaire = populaire;
    }
}
