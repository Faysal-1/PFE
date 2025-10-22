package org.baeldung.persistence.dao;

import org.baeldung.persistence.model.hebergementModel.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Paiement findByNumeroReservation(String numeroReservation);
    Paiement findTopByOrderByIdDesc();
}
