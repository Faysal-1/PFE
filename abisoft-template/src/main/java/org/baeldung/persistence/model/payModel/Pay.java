package org.baeldung.persistence.model.payModel;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pays")
public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom; // Nom du pays
    
    // Explicit getters and setters
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
}
