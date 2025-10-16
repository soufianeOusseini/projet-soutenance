package com.transi.flex.colis.repository;

import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.colis.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColisRepository extends JpaRepository<Colis, Long> {
    List<Colis> findByAgencyId(Long id);

    long countByStatusAndAgencyId(ColisStatus status, Long agencyId);

    long countByAgencyId(Long agencyId);

    List<Colis> findByUserId(Long id);
}
