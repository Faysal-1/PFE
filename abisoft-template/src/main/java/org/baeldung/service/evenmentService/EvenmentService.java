package org.baeldung.service.evenmentService;

import org.baeldung.persistence.model.evenmentModel.Evenment;
import org.baeldung.persistence.model.evenmentModelTarifType.EvenementTarifType;
import org.baeldung.persistence.model.evenmentModelType.EvenmentType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;
import java.time.LocalDate;
import java.util.List;

public interface EvenmentService {
    // Événements
    List<Evenment> getAllEvenements();
    List<Evenment> getEvenementsByType(EvenmentType type);
    List<EvenmentType> getAllTypes();
    Evenment getEvenmentById(Long id);
    Evenment saveEvenment(Evenment evenment);
    void deleteEvenment(Long id);

    // Filtres avancés
    List<Evenment> filtrerEvenements(
        List<String> types,
        List<String> dates,
        List<String> prices,
        List<Ville> villes,
        Double minPrice,
        Double maxPrice
    );

    // Recherche par attributs
    List<Evenment> getEvenementsByVille(Ville ville);
    List<Evenment> getEvenementsByDate(LocalDate date);
    List<Evenment> getEvenementsByOrganisateur(String organisateur);
    List<Evenment> getEvenementsPopulaires();
    List<Evenment> getEvenementsActifs(boolean actif);
    List<Evenment> getEvenementsParNoteMin(Double noteMin);
    List<Evenment> getEvenementsPayants();
    List<Evenment> getEvenementsByTarifType(EvenementTarifType tarifType);
    List<Evenment> getEvenementsByDateDebut(LocalDate dateDebut);
    List<Evenment> getEvenementsByDateFin(LocalDate dateFin);
    List<Evenment> getEvenementsByDateDebutAndFin(LocalDate dateDebut, LocalDate dateFin);
    List<Evenment> getEvenementsByCodePostal(String codePostal);

    // Gestion des villes
    List<Ville> getAllVilles();
    Ville getVilleByNom(String nom);
    List<Ville> convertToVilles(List<String> nomsVilles);

    // Gestion des pays
    List<Pay> getAllPays();
    Pay getPayByNom(String nom);
    List<Pay> convertToPays(List<String> nomsPays);
}
