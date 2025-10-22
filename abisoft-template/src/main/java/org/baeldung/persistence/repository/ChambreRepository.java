package org.baeldung.persistence.repository;

import org.baeldung.persistence.model.hebergementModel.Chambre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChambreRepository extends JpaRepository<Chambre, Long> {
    List<Chambre> findByHebergementId(Long hebergementId);
}
