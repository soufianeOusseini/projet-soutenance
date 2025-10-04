package com.transi.flex.company.repository;

import com.transi.flex.company.enums.CompanyStatus;
import com.transi.flex.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository  extends JpaRepository<Company, Long> {

    Optional<Company> findByEmail(String email);

    List<Company> findByStatus(CompanyStatus status);

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Company> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT c FROM Company c WHERE c.city = :city AND c.status = :status")
    List<Company> findByCityAndStatus(@Param("city") String city, @Param("status") CompanyStatus status);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.status = :status")
    long countByStatus(@Param("status") CompanyStatus status);
}
