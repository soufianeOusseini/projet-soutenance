package com.transi.flex.billings.dao;

import com.transi.flex.billings.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanDAO extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByName(String name);

    List<SubscriptionPlan> findByActiveTrue();
}
