package org.baeldung.persistence.dao;


import org.baeldung.persistence.model.AdresseMac;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdresseMacRepository extends JpaRepository<AdresseMac, Long> {
    List<AdresseMac> findByUserId(Long userId);
}

