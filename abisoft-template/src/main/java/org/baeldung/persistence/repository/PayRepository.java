package org.baeldung.persistence.repository;

import org.baeldung.persistence.model.payModel.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PayRepository extends JpaRepository<Pay, Long> {
    Optional<Pay> findByNomIgnoreCase(String nom);
}
