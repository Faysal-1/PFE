package org.baeldung.persistence.repository;

import org.baeldung.persistence.model.evenmentModel.Evenment;
import org.baeldung.persistence.model.evenmentModelType.EvenmentType;
import org.baeldung.persistence.model.evenmentModelTarifType.EvenementTarifType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EvenmentRepository extends JpaRepository<Evenment, Long> {
    List<Evenment> findAll();
    List<Evenment> findByType(EvenmentType type);
    List<Evenment> findByVille(Ville ville);
    List<Evenment> findByPays(Pay pays);
    List<Evenment> findByTarifType(EvenementTarifType tarifType);
    List<Evenment> findByDateDebut(LocalDate dateDebut);
    List<Evenment> findByDateFin(LocalDate dateFin);
    List<Evenment> findByActif(boolean actif);
    List<Evenment> findByCodePostal(String codePostal);
    List<Evenment> findByOrganisateur(String organisateur);
    List<Evenment> findByNbEtoilesGreaterThanEqual(Double nbEtoiles);
    // Ajoutez d'autres méthodes selon vos besoins
}
