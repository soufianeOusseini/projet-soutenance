package com.transi.flex.payments.repositories;


import com.transi.flex.payments.entities.DepositRequestPaygate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepositRequestDao extends JpaRepository<DepositRequestPaygate, UUID> {
}
