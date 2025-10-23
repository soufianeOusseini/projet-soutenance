package com.transi.flex.billings.dao;

import com.transi.flex.billings.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionDAO extends JpaRepository<Subscription, Long> {
    List<Subscription> findByCompanyId(Long companyId);

    @Query("SELECT s FROM Subscription s WHERE s.company.id = :companyId AND s.active = true AND s.endDate >= :currentDate")
    Optional<Subscription> findActiveSubscriptionByCompanyId(Long companyId, LocalDate currentDate);

    @Query("SELECT s FROM Subscription s WHERE s.active = true AND s.endDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsExpiringBetween(LocalDate startDate, LocalDate endDate);

    List<Subscription> findByActiveTrueAndEndDateBefore(LocalDate date);
}
