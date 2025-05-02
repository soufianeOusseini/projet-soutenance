package com.transi.flex.reservation.repository;

import com.transi.flex.reservation.enums.ReservationStatus;
import com.transi.flex.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByTrajetId(Long trajetId);

    List<Reservation> findByDateBetween(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT COALESCE(SUM(r.nombrePlace), 0) FROM Reservation r WHERE r.trajet.id = :trajetId AND r.status <> 'ANNULEE'")
    Integer sumNombrePlaceByTrajetId(@Param("trajetId") Long trajetId);

    Optional<Reservation> findByTicketId(Long ticketId);

    Long countByTrajetId(Long trajetId);

    List<Reservation> findByDateAndStatus(LocalDate date, ReservationStatus status);

    List<Reservation> findByDate(LocalDate date);

    List<Reservation> findByDateAfter(LocalDate date);

    List<Reservation> findByDateBefore(LocalDate date);
}
