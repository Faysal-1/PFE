package org.baeldung.persistence.repository;

import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.baeldung.persistence.model.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Trouver toutes les notifications pour un utilisateur spécifique
    List<Notification> findByDestinataire(User destinataire);
    
    // Trouver toutes les notifications pour un utilisateur spécifique avec pagination
    Page<Notification> findByDestinataire(User destinataire, Pageable pageable);
    
    // Trouver les notifications non lues pour un utilisateur
    List<Notification> findByDestinataireAndLuFalse(User destinataire);
    
    // Trouver les notifications triées par date de création décroissante
    List<Notification> findByDestinataireOrderByDateCreationDesc(User destinataire);
    
    // Trouver les notifications non lues triées par date de création décroissante
    List<Notification> findByDestinataireAndLuOrderByDateCreationDesc(User destinataire, boolean lu);
    
    // Compter les notifications non lues pour un utilisateur
    long countByDestinataireAndLu(User destinataire, boolean lu);
    
    // Trouver les notifications par type
    List<Notification> findByDestinataireAndType(User destinataire, String type);
    
    // Trouver les notifications liées à un hébergement spécifique
    List<Notification> findByHebergement(Hebergement hebergement);
    
    // Trouver les notifications liées à un hébergement spécifique par son ID
    @Query("SELECT n FROM Notification n WHERE n.hebergement.id = :hebergementId")
    List<Notification> findByHebergementId(@Param("hebergementId") Long hebergementId);
    
    // Trouver les notifications créées après une certaine date
    List<Notification> findByDestinataireAndDateCreationAfter(User destinataire, LocalDateTime date);
    
    // Marquer toutes les notifications d'un utilisateur comme lues
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.lu = true WHERE n.destinataire = :destinataire AND n.lu = false")
    int markAllAsRead(@Param("destinataire") User destinataire);
    
    // Supprimer les notifications plus anciennes qu'une certaine date
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.dateCreation < :date")
    int deleteOldNotifications(@Param("date") LocalDateTime date);
    
    // Trouver les notifications par utilisateur et par hébergement
    List<Notification> findByDestinataireAndHebergement(User destinataire, Hebergement hebergement);
    
    // Recherche de notifications par mot-clé dans le message
    @Query("SELECT n FROM Notification n WHERE n.destinataire = :destinataire AND LOWER(n.message) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Notification> searchByKeyword(@Param("destinataire") User destinataire, @Param("keyword") String keyword);
}