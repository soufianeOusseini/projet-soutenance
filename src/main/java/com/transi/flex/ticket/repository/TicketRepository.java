package com.transi.flex.ticket.repository;

import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findByNumero(String numero);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByTrajetId(Long trajetId);

    List<Ticket> findByUserId(Long userId);

    List<Ticket> findByCompanyId(Long id);


    List<Ticket> findByTypeTransaction(String typeTransaction);

    // Requête pour trouver les réservations expirées
    @Query("SELECT t FROM Ticket t WHERE t.typeTransaction = 'RESERVATION' " +
            "AND t.dateLimitePaiement < :now " +
            "AND (t.status = 'RESERVE' OR t.status = 'EN_ATTENTE')")
    List<Ticket> findExpiredReservations(@Param("now") LocalDateTime now);

    // Requête pour statistiques
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND t.company.id = :companyId")
    Long countByStatusAndCompanyId(@Param("status") TicketStatus status, @Param("companyId") Long companyId);

    @Query("SELECT SUM(t.prix) FROM Ticket t WHERE t.status = 'PAYE' AND t.company.id = :companyId")
    Double getTotalRevenueByCompanyId(@Param("companyId") Long companyId);

    // Tickets d'une date spécifique
    @Query("SELECT t FROM Ticket t WHERE t.date = :date AND t.trajet.id = :trajetId")
    List<Ticket> findByDateAndTrajetId(@Param("date") java.time.LocalDate date, @Param("trajetId") Long trajetId);

    long countByDateGreaterThanEqualAndCompanyId(LocalDate date, Long companyId);

    long countByDateBetweenAndCompanyId(LocalDate startDate, LocalDate endDate, Long companyId);

    long countByCompanyId(Long companyId);
}
