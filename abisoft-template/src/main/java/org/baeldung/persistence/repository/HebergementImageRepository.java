package org.baeldung.persistence.repository;

import org.baeldung.persistence.model.hebergementModel.HebergementImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HebergementImageRepository extends JpaRepository<HebergementImage, Long> {
}
