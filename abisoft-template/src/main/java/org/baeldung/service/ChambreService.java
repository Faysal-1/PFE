package org.baeldung.service;

import org.baeldung.persistence.model.hebergementModel.Chambre;
import java.util.List;

public interface ChambreService {
    List<Chambre> getToutesLesChambres();
    List<Chambre> getChambresParHebergement(Long hebergementId);
}
