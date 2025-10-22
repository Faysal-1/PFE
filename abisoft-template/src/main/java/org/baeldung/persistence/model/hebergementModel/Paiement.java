package org.baeldung.persistence.model.hebergementModel;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numeroReservation;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String telephone;
    
    @Column(nullable = false)
    private String nomCarte;
    
    @Column(nullable = false)
    private String numeroCarte;
    
    @Column(nullable = false)
    private String moisExpiration;
    
    @Column(nullable = false)
    private String anneeExpiration;
    
    @Column(nullable = false)
    private String ccv;
    
    @Column(nullable = false)
    private String pays;
    
    @Column(nullable = false)
    private String adresse1;
    
    private String adresse2;
    
    @Column(nullable = false)
    private String ville;
    
    private String etat;
    
    @Column(nullable = false)
    private String codePostal;
    
    @Column(nullable = false)
    private Double montantTotal;
    
    @Column(nullable = false)
    private LocalDateTime dateReservation;
    
    private String servicesSelectionnes;

    // Constructeurs
    public Paiement() {
        this.dateReservation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroReservation() {
        return numeroReservation;
    }

    public void setNumeroReservation(String numeroReservation) {
        this.numeroReservation = numeroReservation;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public String getNomCarte() {
        return nomCarte;
    }

    public void setNomCarte(String nomCarte) {
        this.nomCarte = nomCarte;
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public String getMoisExpiration() {
        return moisExpiration;
    }

    public void setMoisExpiration(String moisExpiration) {
        this.moisExpiration = moisExpiration;
    }

    public String getAnneeExpiration() {
        return anneeExpiration;
    }

    public void setAnneeExpiration(String anneeExpiration) {
        this.anneeExpiration = anneeExpiration;
    }

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getAdresse1() {
        return adresse1;
    }

    public void setAdresse1(String adresse1) {
        this.adresse1 = adresse1;
    }

    public String getAdresse2() {
        return adresse2;
    }

    public void setAdresse2(String adresse2) {
        this.adresse2 = adresse2;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getServicesSelectionnes() {
        return servicesSelectionnes;
    }

    public void setServicesSelectionnes(String servicesSelectionnes) {
        this.servicesSelectionnes = servicesSelectionnes;
    }
}
