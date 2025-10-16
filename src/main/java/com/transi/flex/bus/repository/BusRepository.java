package com.transi.flex.bus.repository;

import com.transi.flex.bus.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus,Long> {
    List<Bus> findByAgencyId(Long id);

    boolean existsByNumberAndAgencyId(String numero, Long agencyId);

    boolean existsByNumberAndAgencyIdAndIdNot(String number, Long agencyId, Long id);

}
