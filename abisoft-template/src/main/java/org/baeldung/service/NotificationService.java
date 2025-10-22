package org.baeldung.service;

import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.baeldung.persistence.model.notification.Notification;
import org.baeldung.persistence.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Envoie une notification à tous les super admins pour un nouvel hébergement
     */
    public void notifierNouvelHebergement(Hebergement hebergement) {
        // Trouver tous les super admins
        List<User> superAdmins = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_SUPERADMIN")))
                .collect(Collectors.toList());

        // Créer une notification pour chaque super admin
        for (User admin : superAdmins) {
            String message = "Nouvel hébergement ajouté par " + 
                    (hebergement.getProprietaire() != null ? hebergement.getProprietaire().getEmail() : "un propriétaire") + 
                    " : " + hebergement.getNom() + " (" + hebergement.getType() + ")";
            
            Notification notification = new Notification(hebergement, admin, message, "NOUVEL_HEBERGEMENT");
            notificationRepository.save(notification);
        }
    }
    
    /**
     * Envoie une notification à un propriétaire lorsque son hébergement est validé ou refusé
     */
    public void notifierStatutHebergement(Hebergement hebergement, boolean accepte) {
        // Vérifier que l'hébergement a un propriétaire
        if (hebergement.getProprietaire() == null) {
            return;
        }
        
        // Créer le message approprié
        String type = accepte ? "HEBERGEMENT_ACCEPTE" : "HEBERGEMENT_REFUSE";
        String message;
        
        if (accepte) {
            message = "Votre hébergement '" + hebergement.getNom() + "' a été validé et est maintenant visible sur le site.";
        } else {
            message = "Votre hébergement '" + hebergement.getNom() + "' a été refusé. " + 
                    (hebergement.getCommentaireRefus() != null && !hebergement.getCommentaireRefus().isEmpty() ? 
                    "Motif: " + hebergement.getCommentaireRefus() : "Veuillez contacter l'administration pour plus d'informations.");
        }
        
        // Créer et enregistrer la notification
        Notification notification = new Notification(hebergement, hebergement.getProprietaire(), message, type);
        notificationRepository.save(notification);
    }

    /**
     * Récupère les notifications non lues pour un utilisateur
     */
    public List<Notification> getNotificationsNonLues(User user) {
        return notificationRepository.findByDestinataireAndLuOrderByDateCreationDesc(user, false);
    }

    /**
     * Récupère toutes les notifications pour un utilisateur
     */
    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByDestinataireOrderByDateCreationDesc(user);
    }

    /**
     * Marque une notification comme lue
     */
    public void marquerCommeLue(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            // Utiliser la méthode générée par Lombok
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * Compte les notifications non lues pour un utilisateur
     */
    public long countNotificationsNonLues(User user) {
        return notificationRepository.countByDestinataireAndLu(user, false);
    }
    
    /**
     * Trouve toutes les notifications pour un destinataire
     */
    public List<Notification> findByDestinataire(User user) {
        return notificationRepository.findByDestinataireOrderByDateCreationDesc(user);
    }
    
    /**
     * Compte les notifications non lues pour un utilisateur (alias pour countNotificationsNonLues)
     */
    public int countUnreadNotifications(User user) {
        return (int) countNotificationsNonLues(user);
    }
    
    /**
     * Supprime toutes les notifications associées à un hébergement
     * @param hebergementId L'ID de l'hébergement dont les notifications doivent être supprimées
     * @return Le nombre de notifications supprimées
     */
    public int supprimerNotificationsParHebergement(Long hebergementId) {
        List<Notification> notifications = notificationRepository.findByHebergementId(hebergementId);
        int count = notifications.size();
        notificationRepository.deleteAll(notifications);
        return count;
    }
}
