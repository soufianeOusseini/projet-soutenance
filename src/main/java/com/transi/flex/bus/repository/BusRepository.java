package com.transi.flex.bus.repository;

import com.transi.flex.bus.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus,Long> {
    List<Bus> findByCompanyId(Long id);

    boolean existsByNumberAndCompanyId(String numero, Long companyId);

    boolean existsByNumberAndCompanyIdAndIdNot(String number, Long companyId, Long id);

}
