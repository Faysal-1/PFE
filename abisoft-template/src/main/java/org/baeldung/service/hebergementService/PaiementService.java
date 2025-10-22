package org.baeldung.service.hebergementService;

import org.baeldung.persistence.model.hebergementModel.Paiement;

public interface PaiementService {
    Paiement savePaiement(Paiement paiement);
    Paiement getPaiementByNumeroReservation(String numeroReservation);
    Paiement getDernierPaiement();
}
