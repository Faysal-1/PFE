package org.baeldung.persistence.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import org.baeldung.persistence.model.payModel.Pay;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

@Data
@Entity
@Table(name = "user_account")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String tel; // ancien champ, à remplacer par phone

    private String phone; // pour correspondre au champ du formulaire

    private String adresse;
    private String ville;
    private String codePostal;
    
    @ManyToOne
    @JoinColumn(name = "pays_id")
    private Pay pays;
    
    private String photoUrl; // chemin ou url de la photo
    private java.time.LocalDate dateNaissance;

    @Column(length = 60)
    private String password;

    private boolean enabled;

    private boolean isUsing2FA;

    private String secret;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    // Explicit getter and setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    // Explicit getter and setter for email
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Explicit getter and setter for roles
    public Collection<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
    
    // Explicit getter and setter for enabled
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    // Explicit getters and setters for firstName and lastName
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    // Explicit getters and setters for isUsing2FA and secret
    public boolean isUsing2FA() {
        return isUsing2FA;
    }
    
    public void setUsing2FA(boolean isUsing2FA) {
        this.isUsing2FA = isUsing2FA;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    // Explicit getter and setter for id
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    // Explicit getters and setters for tel, phone, adresse, ville, codePostal, pays, photoProfil, dateNaissance
    public String getTel() {
        return tel;
    }
    
    public void setTel(String tel) {
        this.tel = tel;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    public String getCodePostal() {
        return codePostal;
    }
    
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    
    public Pay getPays() {
        return pays;
    }
    
    public void setPays(Pay pays) {
        this.pays = pays;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    
    // Pour la compatibilité avec l'ancien code
    public String getPhotoProfil() {
        return photoUrl;
    }
    
    public void setPhotoProfil(String photoProfil) {
        this.photoUrl = photoProfil;
    }
    
    public java.time.LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(java.time.LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    /**
     * Retourne l'URL formatée de la photo de profil
     * Si photoUrl est null ou vide, retourne une image par défaut
     * @return URL formatée de la photo de profil
     */
    public String getPhotoUrlFormatted() {
        if (photoUrl == null || photoUrl.isEmpty()) {
            return "/assets/img/tourist_guide_pic.jpg";
        }
        return photoUrl;
    }
}