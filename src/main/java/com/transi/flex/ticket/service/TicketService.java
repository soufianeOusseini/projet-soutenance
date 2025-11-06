package com.transi.flex.ticket.service;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.account.service.UserService;
import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.pdf.PdfTicketService;
import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.ticket.mapper.TicketMapper;
import com.transi.flex.ticket.model.Ticket;
import com.transi.flex.ticket.repository.TicketRepository;
import com.transi.flex.trajet.model.Trajet;
import com.transi.flex.trajet.repository.TrajetRepository;
import com.transi.flex.tripSchedule.dao.TripScheduleDAO;
import com.transi.flex.tripSchedule.model.TripSchedule;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class TicketService {
    private final TicketRepository repository;
    private final TicketMapper mapper;
    private final UserRepository userRepository;
    private final TrajetRepository trajetRepository;
    private final TripScheduleDAO tripScheduleDAO;
    private final PdfTicketService pdfTicketService;
    private final UserService userService;

    @Transactional
    public TicketDTO save(TicketDTO ticketDTO) {
        UserDTO user = null;
        if(ticketDTO.getUserId() !=null){
            user = userService.getUserById(ticketDTO.getUserId());

        }

        Trajet trajet = trajetRepository.findById(ticketDTO.getTrajetId()).orElse(null);
        if (trajet == null) {
            throw new EntityNotFoundException("Trajet non trouvé avec l'ID: " + ticketDTO.getTrajetId());
        }

        TripSchedule schedule = tripScheduleDAO
                .findByTrajetIdAndDate(ticketDTO.getTrajetId(), ticketDTO.getDate())
                .orElseThrow(() -> new EntityNotFoundException("Aucune planification trouvée pour ce trajet à cette date"));

        if (schedule.getNombrePlacesDisponibles() <= 0) {
            throw new IllegalStateException("Aucune place disponible pour ce trajet");
        }

        if (ticketDTO.getSeatNumber() != null) {
            // Vérifier si le siège est déjà occupé
            boolean seatTaken = repository.existsByTrajetIdAndDateAndSeatNumber(
                    ticketDTO.getTrajetId(),
                    ticketDTO.getDate(),
                    ticketDTO.getSeatNumber()
            );

            if (seatTaken) {
                throw new IllegalStateException("Le siège n°" + ticketDTO.getSeatNumber() + " est déjà occupé");
            }

            // Vérifier que le numéro de siège ne dépasse pas la capacité du bus
            int capaciteTotale = schedule.getBus().getCapacity(); // Assurez-vous que Bus a un champ capacite
            if (ticketDTO.getSeatNumber() > capaciteTotale) {
                throw new IllegalStateException("Le numéro de siège ne peut pas dépasser " + capaciteTotale);
            }
        } else {
            // Attribuer automatiquement le prochain siège disponible
            Integer nextSeatNumber = getNextAvailableSeatNumber(ticketDTO.getTrajetId(), ticketDTO.getDate(), schedule);
            ticketDTO.setSeatNumber(nextSeatNumber);
        }


        if (ticketDTO.getNumero() == null || ticketDTO.getNumero().isEmpty()) {
            ticketDTO.setNumero("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        ticketDTO.setPrix(schedule.getPrix());
        ticketDTO.setHeureDepart(schedule.getHeureDepart());
        ticketDTO.setDate(schedule.getDateDepart());

        if (user != null) {
            ticketDTO.setUser(user);
        }

        if ("RESERVATION".equals(ticketDTO.getTypeTransaction())) {
            ticketDTO.setStatus(TicketStatus.RESERVE);

            // Deadline = 1h avant le départ à la date du ticket
            LocalDate dateDuTicket = schedule.getDateDepart();               // ou ticketDTO.getDate() si tu préfères
            LocalTime heureDepart   = schedule.getHeureDepart();

            LocalDateTime deadline;
            if (dateDuTicket != null && heureDepart != null) {
                deadline = LocalDateTime.of(dateDuTicket, heureDepart).minusHours(1);
                // Si jamais c'est déjà passé (réservation tardive), on met au moins 1h à partir de maintenant
                if (deadline.isBefore(LocalDateTime.now())) {
                    deadline = LocalDateTime.now().plusHours(1);
                }
            } else {
                // Filet de sécurité si la planif est incomplète
                deadline = LocalDateTime.now().plusHours(1);
            }
            ticketDTO.setDateLimitePaiement(deadline);

        } else {
            ticketDTO.setStatus(TicketStatus.PAYE);
            ticketDTO.setTypeTransaction("ACHAT");
        }

        Ticket ticket = mapper.toModel(ticketDTO);
        ticket.setTrajet(trajet);
        ticket.setAgency(schedule.getAgency());

        Ticket savedTicket = repository.save(ticket);

        if ("ACHAT".equals(ticketDTO.getTypeTransaction())) {
            schedule.setNombrePlacesDisponibles(schedule.getNombrePlacesDisponibles() - 1);
            tripScheduleDAO.save(schedule);
        }

        return mapper.toDto(savedTicket);
    }

    @Transactional
    public TicketDTO confirmReservation(Long ticketId, String modePaiement) {
        Ticket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + ticketId));

        if (ticket.getStatus() != TicketStatus.RESERVE) {
            throw new IllegalStateException("Ce ticket n'est pas une réservation");
        }

        if (ticket.isReservationExpired()) {
            throw new IllegalStateException("Cette réservation a expiré");
        }

        // Vérifier qu'il y a encore des places
        TripSchedule schedule = tripScheduleDAO.findByTrajetIdAndDate(ticket.getTrajet().getId(), ticket.getDate())
                .orElseThrow(() -> new EntityNotFoundException("Planification non trouvée"));

        if (schedule.getNombrePlacesDisponibles() <= 0) {
            throw new IllegalStateException("Plus de places disponibles pour ce trajet");
        }

        // Confirmer la réservation
        ticket.setStatus(TicketStatus.PAYE);
        ticket.setTypeTransaction("ACHAT");

        // Diminuer les places disponibles
        schedule.setNombrePlacesDisponibles(schedule.getNombrePlacesDisponibles() - 1);
        tripScheduleDAO.save(schedule);

        Ticket updatedTicket = repository.save(ticket);
        return mapper.toDto(updatedTicket);
    }

    @Transactional
    public TicketDTO cancelTicket(Long id, String cancellationReason, String comment) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + id));

        if (ticket.getStatus() == TicketStatus.UTILISE) {
            throw new IllegalStateException("Impossible d'annuler un ticket déjà utilisé");
        }

        if (ticket.getStatus() == TicketStatus.ANNULE) {
            throw new IllegalStateException("Ce ticket est déjà annulé");
        }

        // Récupérer la planification correspondante pour remettre la place
        if (ticket.getStatus() == TicketStatus.PAYE || "ACHAT".equals(ticket.getTypeTransaction())) {
            TripSchedule schedule = tripScheduleDAO.findByTrajetIdAndDate(ticket.getTrajet().getId(), ticket.getDate())
                    .orElse(null);

            if (schedule != null) {
                schedule.setNombrePlacesDisponibles(schedule.getNombrePlacesDisponibles() + 1);
                tripScheduleDAO.save(schedule);
            }
        }

        // Sauvegarder les informations d'annulation
        ticket.setStatus(TicketStatus.ANNULE);
        ticket.setCancellation_reason(cancellationReason);
        ticket.setComment(comment);

        Ticket updatedTicket = repository.save(ticket);

        return mapper.toDto(updatedTicket);
    }

    public byte[] generateTicketPdf(Long ticketId) {
        Ticket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + ticketId));

        return pdfTicketService.generateTicketPdf(ticket);
    }

    public List<TicketDTO> getAll() {
        return mapper.toDtos(repository.findByAgencyId(AgencyContextHolder.getCurrentAgencyId()));
    }

    public TicketDTO getTicketById(Long id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + id));
        return mapper.toDto(ticket);
    }

    @Transactional
    public TicketDTO useTicket(Long id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + id));

        if (ticket.getStatus() == TicketStatus.ANNULE) {
            throw new IllegalStateException("Impossible d'utiliser un ticket annulé");
        }
        if (ticket.getStatus() == TicketStatus.UTILISE) {
            throw new IllegalStateException("Ce ticket a déjà été utilisé");
        }
        if (ticket.getStatus() == TicketStatus.EXPIRE) {
            throw new IllegalStateException("Impossible d'utiliser un ticket expiré");
        }
        if (ticket.getStatus() == TicketStatus.RESERVE) {
            throw new IllegalStateException("Ce ticket est une réservation non confirmée");
        }

        ticket.setStatus(TicketStatus.UTILISE);
        Ticket updatedTicket = repository.save(ticket);
        return mapper.toDto(updatedTicket);
    }

    // Méthode pour expirer automatiquement les réservations (à appeler via un job cron)
    @Transactional
    public void expireReservations() {
        List<Ticket> expiredReservations = repository.findExpiredReservations(LocalDateTime.now());

        for (Ticket ticket : expiredReservations) {
            ticket.setStatus(TicketStatus.EXPIRE);
            repository.save(ticket);
        }
    }

    public List<TicketDTO> getTicketsByUserId(Long userId) {
        List<Ticket> tickets = repository.findByUserId(userId);
        return mapper.toDtos(tickets);
    }

    public List<TicketDTO> getTicketsByTrajetId(Long trajetId) {
        List<Ticket> tickets = repository.findByTrajetId(trajetId);
        return mapper.toDtos(tickets);
    }

    public List<TicketDTO> getTicketsByStatus(TicketStatus status) {
        List<Ticket> tickets = repository.findByStatus(status);
        return mapper.toDtos(tickets);
    }

    @Transactional
    public TicketDTO validateTicketByNumero(String numero) {
        Ticket ticket = repository.findByNumero(numero)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec le numéro: " + numero));

        if (ticket.getStatus() == TicketStatus.EN_ATTENTE) {
            ticket.setStatus(TicketStatus.VALIDE);
            ticket = repository.save(ticket);
        } else if (ticket.getStatus() != TicketStatus.VALIDE) {
            throw new IllegalStateException("Le ticket ne peut pas être validé car son statut est: " + ticket.getStatus());
        }

        return mapper.toDto(ticket);
    }

    public List<TicketDTO> getByUser(Long id){
        return mapper.toDtos(repository.findByUserId(id));
    }

    public List<TicketDTO> getTicketsByUser() {
        try {
                UserDTO currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                throw new EntityNotFoundException("Utilisateur non trouvé");
            }
            List<Ticket> tickets = repository.findByUserId(currentUser.getId());
            return mapper.toDtos(tickets);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des tickets: " + e.getMessage());
        }
    }

    private Integer getNextAvailableSeatNumber(Long trajetId, LocalDate date, TripSchedule schedule) {
        // Récupérer tous les numéros de sièges déjà occupés pour ce trajet et cette date
        List<Integer> occupiedSeats = repository.findOccupiedSeatNumbers(trajetId, date);

        int capaciteTotale = schedule.getBus().getCapacity(); // Assurez-vous que Bus a un champ capacite

        // Trouver le premier siège disponible
        for (int i = 1; i <= capaciteTotale; i++) {
            if (!occupiedSeats.contains(i)) {
                return i;
            }
        }

        throw new IllegalStateException("Aucun siège disponible");
    }

    /**
     * Récupère la liste des sièges occupés pour un trajet et une date donnés
     */
    public List<Integer> getOccupiedSeats(Long trajetId, LocalDate date) {
        return repository.findOccupiedSeatNumbers(trajetId, date);
    }
}