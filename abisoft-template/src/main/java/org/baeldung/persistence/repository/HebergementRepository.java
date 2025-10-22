package org.baeldung.persistence.repository;

import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.baeldung.persistence.model.hebergementModel.ValidationStatus;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.persistence.model.hebergementModelTarifType.HebergementTarifType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;
import org.baeldung.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HebergementRepository extends JpaRepository<Hebergement, Long> {
    // Méthodes pour récupérer les hébergements
    List<Hebergement> findByHebergementType(HebergementType type);
    List<Hebergement> findByVille(Ville ville);
    List<Hebergement> findByPays(Pay pays);
    List<Hebergement> findByTarifType(HebergementTarifType tarifType);
    List<Hebergement> findByTarifTypeNot(HebergementTarifType tarifType);
    List<Hebergement> findByDateDebut(LocalDate dateDebut);
    List<Hebergement> findByDateFin(LocalDate dateFin);
    List<Hebergement> findByActif(boolean actif);
    List<Hebergement> findByCodePostal(String codePostal);
    List<Hebergement> findByDateDebutLessThanEqualAndDateFinGreaterThanEqual(LocalDate dateDebut, LocalDate dateFin);
    List<Hebergement> findByDateDebutGreaterThanEqualAndDateFinLessThanEqual(LocalDate dateDebut, LocalDate dateFin);
    
    // Méthodes pour trouver les hébergements par propriétaire
    List<Hebergement> findByProprietaireId(Long proprietaireId);
    List<Hebergement> findByProprietaire(User proprietaire);
    List<Hebergement> findByProprietaireEmail(String email);
    List<Hebergement> findByProprietaireEmailContainingIgnoreCase(String email);
    
    // Méthodes pour filtrer par statut de validation
    List<Hebergement> findByValidationStatus(ValidationStatus validationStatus);
    List<Hebergement> findByProprietaireIdAndValidationStatus(Long proprietaireId, ValidationStatus validationStatus);
    List<Hebergement> findByProprietaireAndValidationStatus(User proprietaire, ValidationStatus validationStatus);
    
    // Méthode pour filtrer par type d'hébergement et propriétaire
    List<Hebergement> findByProprietaireIdAndType(Long proprietaireId, String type);
    
    // Méthode pour compter les hébergements en attente
    long countByValidationStatus(ValidationStatus validationStatus);
    long countByProprietaireAndValidationStatus(User proprietaire, ValidationStatus validationStatus);
}
