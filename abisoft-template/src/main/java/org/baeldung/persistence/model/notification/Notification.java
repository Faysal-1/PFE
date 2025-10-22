package org.baeldung.persistence.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.hebergementModel.Hebergement;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hebergement_id")
    private Hebergement hebergement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User destinataire;

    private String message;

    private LocalDateTime dateCreation = LocalDateTime.now();

    private boolean lu = false;

    private String type;

    // Constructeur personnalisé
    public Notification(Hebergement hebergement, User destinataire, String message, String type) {
        this.hebergement = hebergement;
        this.destinataire = destinataire;
        this.message = message;
        this.type = type;
    }
    
    // Les getters et setters sont générés automatiquement par Lombok (@Data)
    
    // Méthodes explicites pour éviter les problèmes de compatibilité avec Lombok
    public boolean getLu() {
        return this.lu;
    }
    
    public void setLu(boolean lu) {
        this.lu = lu;
    }
    
    // Alias pour la compatibilité avec les conventions JavaBean pour les booléens
    public boolean isLu() {
        return this.lu;
    }
}
