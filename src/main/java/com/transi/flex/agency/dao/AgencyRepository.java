package com.transi.flex.agency.repository;

import com.transi.flex.agency.model.Agency;
import com.transi.flex.agency.enums.AgencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {

    List<Agency> findByCompanyId(Long companyId);

    List<Agency> findByCompanyIdAndStatus(Long companyId, AgencyStatus status);

    Optional<Agency> findByCode(String code);

    Optional<Agency> findByCodeAndCompanyId(String code, Long companyId);

    @Query("SELECT COUNT(a) FROM Agency a WHERE a.company.id = :companyId AND a.status = :status")
    long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") AgencyStatus status);

    boolean existsByCodeAndCompanyId(String code, Long companyId);

    List<Agency> findByCompanyIdAndCityContainingIgnoreCase(Long companyId, String city);

    @Query("SELECT a FROM Agency a WHERE a.company.id = :companyId AND " +
            "(LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Agency> searchByCompanyIdAndKeyword(@Param("companyId") Long companyId, @Param("search") String search);
}