package com.transi.flex.company.repository;

import com.transi.flex.company.model.CompanyAccount;
import com.transi.flex.company.enums.AccountStatus;
import com.transi.flex.company.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyAccountRepository extends JpaRepository<CompanyAccount, Long> {

    List<CompanyAccount> findByAgencyId(Long id);

    @Query("SELECT SUM(ca.balance) FROM CompanyAccount ca WHERE ca.agency.id = :agencyId AND ca.status = 'ACTIVE'")
    BigDecimal getTotalBalanceByCompanyId(@Param("agencyId") Long agencyId);

    @Query("SELECT SUM(ca.creditLimit) FROM CompanyAccount ca WHERE ca.agency.id = :agencyId AND ca.status = 'ACTIVE'")
    BigDecimal getTotalCreditLimitByCompanyId(@Param("agencyId") Long agencyId);

    @Query("SELECT COUNT(ca) FROM CompanyAccount ca WHERE ca.agency.id = :agencyId AND ca.status = :status")
    long countByAgencyIdAndStatus(@Param("agencyId") Long agencyId, @Param("status") AccountStatus status);

    @Query("SELECT ca FROM CompanyAccount ca WHERE ca.agency.id = :agencyId AND ca.type = 'PRINCIPAL' AND ca.status = 'ACTIVE'")
    Optional<CompanyAccount> findPrincipalAccountByAgencyId(@Param("agencyId") Long agencyId);

    boolean existsByAccountNumber(String accountNumber);
}