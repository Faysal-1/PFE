package org.baeldung.service.hebergementService;

import org.baeldung.persistence.dao.PaiementRepository;
import org.baeldung.persistence.model.hebergementModel.Paiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaiementServiceImpl implements PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;
    
    @Override
    public Paiement savePaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }
    
    @Override
    public Paiement getPaiementByNumeroReservation(String numeroReservation) {
        return paiementRepository.findByNumeroReservation(numeroReservation);
    }
    
    @Override
    public Paiement getDernierPaiement() {
        // Récupérer le dernier paiement enregistré (par ID décroissant)
        return paiementRepository.findTopByOrderByIdDesc();
    }
}
