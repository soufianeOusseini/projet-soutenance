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

    List<Ticket> findByAgencyId(Long id);

    List<Ticket> findByTypeTransaction(String typeTransaction);

    @Query("SELECT t FROM Ticket t WHERE t.typeTransaction = 'RESERVATION' " +
            "AND t.dateLimitePaiement < :now " +
            "AND (t.status = 'RESERVE' OR t.status = 'EN_ATTENTE')")
    List<Ticket> findExpiredReservations(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND t.agency.id = :agencyId")
    Long countByStatusAndAgencyId(@Param("status") TicketStatus status, @Param("agencyId") Long agencyId);

    @Query("SELECT SUM(t.prix) FROM Ticket t WHERE t.status = 'PAYE' AND t.agency.id = :agencyId")
    Double getTotalRevenueByAgencyId(@Param("agencyId") Long agencyId);

    // Tickets d'une date spécifique
    @Query("SELECT t FROM Ticket t WHERE t.date = :date AND t.trajet.id = :trajetId")
    List<Ticket> findByDateAndTrajetId(@Param("date") java.time.LocalDate date, @Param("trajetId") Long trajetId);

    long countByDateGreaterThanEqualAndAgencyId(LocalDate date, Long agencyId);

    long countByDateBetweenAndAgencyId(LocalDate startDate, LocalDate endDate, Long agencyId);

    long countByAgencyId(Long agencyId);

    @Query("SELECT SUM(t.prix) FROM Ticket t WHERE t.status = 'PAYE'")
    Double sumTotalSales();

    @Query("SELECT SUM(t.prix) FROM Ticket t WHERE t.status = 'PAYE' " +
            "AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    Double sumSalesByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
