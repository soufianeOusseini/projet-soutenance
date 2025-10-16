package com.transi.flex.driver.dao;

import com.transi.flex.driver.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverDAO extends JpaRepository<Driver, Long> {
    List<Driver> findByAgencyId(Long currentId);
}
