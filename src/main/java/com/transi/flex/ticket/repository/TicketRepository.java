package com.transi.flex.ticket.repository;

import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findByNumero(String numero);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByTrajetId(Long trajetId);

    List<Ticket> findByUserId(Long userId);

    List<Ticket> findByCompanyId(Long id);
}
