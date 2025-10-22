package org.baeldung.service.hebergementService;

import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.baeldung.persistence.model.hebergementModelTarifType.HebergementTarifType;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Handler;

public interface HebergementService {
    List<Hebergement> getAllHebergement();
    List<Hebergement> getHebergementByType(HebergementType type);
    List<HebergementType> getAllTypes();
    Hebergement getHebergementById(Long id);
    Hebergement saveHebergement(Hebergement hebergement);
    void deleteHebergement(Long id);
    // Recherche par attributs
    List<Hebergement> getHebergementByVille(Ville ville);
    List<Hebergement> getHebergementByDate(LocalDate date);
    List<Hebergement> getHebergementByOrganisateur(String organisateur);
    List<Hebergement> getHebergementPopulaires();
    List<Hebergement> getHebergementActifs(boolean actif);
    List<Hebergement> getHebergementParNoteMin(Double noteMin);
    List<Hebergement> getHebergementPayants();
    List<Hebergement> getHebergementByTarifType(HebergementTarifType tarifType);
    List<Hebergement> getHebergementByDateDebut(LocalDate dateDebut);
    List<Hebergement> getHebergementByDateFin(LocalDate dateFin);
    List<Hebergement> getHebergementByDateDebutAndFin(LocalDate dateDebut, LocalDate dateFin);
    List<Hebergement> getHebergementByCodePostal(String codePostal);

    // Gestion des villes
    List<Ville> getAllVilles();
    Ville getVilleByNom(String nom);
    List<Ville> convertToVilles(List<String> nomsVilles);

    // Gestion des pays
    List<Pay> getAllPays();
    Pay getPayByNom(String nom);
    List<Pay> convertToPays(List<String> nomsPays);
}
