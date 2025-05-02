package com.transi.flex.colis.repository;

import com.transi.flex.colis.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColisRepository extends JpaRepository<Colis, Long> {
}
