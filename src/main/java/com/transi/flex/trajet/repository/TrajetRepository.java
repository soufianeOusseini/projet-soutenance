package com.transi.flex.trajet.repository;

import com.transi.flex.trajet.model.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
    List<Trajet> findByCompanyId(Long is);

    long countByCompanyId(Long companyId);
}
