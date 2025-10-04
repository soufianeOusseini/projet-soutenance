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

    List<CompanyAccount> findByCompanyId(Long companyId);

    List<CompanyAccount> findByCompanyIdAndStatus(Long companyId, AccountStatus status);

    List<CompanyAccount> findByCompanyIdAndType(Long companyId, AccountType type);

    Optional<CompanyAccount> findByAccountNumber(String accountNumber);

    @Query("SELECT SUM(ca.balance) FROM CompanyAccount ca WHERE ca.company.id = :companyId AND ca.status = 'ACTIVE'")
    BigDecimal getTotalBalanceByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT SUM(ca.creditLimit) FROM CompanyAccount ca WHERE ca.company.id = :companyId AND ca.status = 'ACTIVE'")
    BigDecimal getTotalCreditLimitByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(ca) FROM CompanyAccount ca WHERE ca.company.id = :companyId AND ca.status = :status")
    long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") AccountStatus status);

    @Query("SELECT ca FROM CompanyAccount ca WHERE ca.company.id = :companyId AND ca.type = 'PRINCIPAL' AND ca.status = 'ACTIVE'")
    Optional<CompanyAccount> findPrincipalAccountByCompanyId(@Param("companyId") Long companyId);

    boolean existsByAccountNumber(String accountNumber);
}