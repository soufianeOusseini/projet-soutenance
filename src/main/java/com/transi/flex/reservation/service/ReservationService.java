package com.transi.flex.reservation.service;

import com.transi.flex.reservation.dto.ReservationDTO;
import com.transi.flex.reservation.enums.ReservationStatus;
import com.transi.flex.reservation.mapper.ReservationMapper;
import com.transi.flex.reservation.model.Reservation;
import com.transi.flex.reservation.repository.ReservationRepository;
import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.mapper.TicketMapper;
import com.transi.flex.ticket.model.Ticket;
import com.transi.flex.ticket.service.TicketService;
import com.transi.flex.trajet.dto.TrajetDTO;
import com.transi.flex.trajet.mapper.TrajetMapper;
import com.transi.flex.trajet.model.Trajet;
import com.transi.flex.trajet.service.TrajetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;
    private final TrajetMapper trajetMapper;
    private final TrajetService trajetService;
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;


    public List<ReservationDTO> getAllReservations() {
        return reservationMapper.toDtos(reservationRepository.findAll());
    }

    public ReservationDTO getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID: " + id));
    }

    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        TrajetDTO trajet = trajetService.getTrajetById(reservationDTO.getTrajetId());

        Reservation reservation = reservationMapper.dtoModel(reservationDTO);

        if (reservation.getDate() == null) {
            reservation.setDate(LocalDate.now());
        }
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.EN_ATTENTE);
        }

        reservation.setTrajet(trajetMapper.toModel(trajet));

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDto(savedReservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Réservation non trouvée avec l'ID: " + id);
        }
        reservationRepository.deleteById(id);
    }


    public List<ReservationDTO> findReservationsByStatus(ReservationStatus status) {
        return reservationMapper.toDtos(reservationRepository.findByStatus(status));
    }

    public List<ReservationDTO> findReservationsByTrajet(Long trajetId) {
        return reservationMapper.toDtos(reservationRepository.findByTrajetId(trajetId));
    }

    public List<ReservationDTO> findReservationsByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return reservationMapper.toDtos(reservationRepository.findByDateBetween(dateDebut, dateFin));
    }


    public Reservation getReservationEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID: " + id));
    }


    @Transactional
    public ReservationDTO cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'ID: " + id));

        if (reservation.getStatus() == ReservationStatus.TERMINEE) {
            throw new IllegalStateException("Impossible d'annuler une réservation déjà terminée");
        }

        reservation.setStatus(ReservationStatus.ANNULEE);

        if (reservation.getTicket() != null) {
            ticketService.cancelTicket(reservation.getTicket().getId());
        }

        Reservation updatedReservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(updatedReservation);
    }

}
