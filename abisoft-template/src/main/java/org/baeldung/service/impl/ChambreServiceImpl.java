package org.baeldung.service.impl;

import org.baeldung.persistence.model.hebergementModel.Chambre;
import org.baeldung.persistence.repository.ChambreRepository;
import org.baeldung.service.ChambreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChambreServiceImpl implements ChambreService {

    @Autowired
    private ChambreRepository chambreRepository;

    @Override
    public List<Chambre> getToutesLesChambres() {
        return chambreRepository.findAll();
    }

    @Override
    public List<Chambre> getChambresParHebergement(Long hebergementId) {
        return chambreRepository.findByHebergementId(hebergementId);
    }
}
