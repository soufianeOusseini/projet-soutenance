package com.transi.flex.ticket.service;

import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.ticket.mapper.TicketMapper;
import com.transi.flex.ticket.model.Ticket;
import com.transi.flex.ticket.repository.TicketRepository;
import com.transi.flex.trajet.dto.TrajetDTO;
import com.transi.flex.trajet.model.Trajet;
import com.transi.flex.trajet.repository.TrajetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class TicketService {
    private final TicketRepository repository;
    private final TicketMapper mapper;
    private final UserRepository userRepository;
    private final TrajetRepository trajetRepository;

    @Transactional
    public TicketDTO save(TicketDTO ticketDTO) {

        Trajet trajet = trajetRepository.findById(ticketDTO.getTrajetId()).orElse(null);
        if (ticketDTO.getNumero() == null || ticketDTO.getNumero().isEmpty()) {
            ticketDTO.setNumero("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        ticketDTO.setStatus(TicketStatus.EN_ATTENTE);

        Ticket ticket = mapper.toModel(ticketDTO) ;
        ticket.setTrajet(trajet);
        return mapper.toDto(repository.save(ticket));
    }

    public List<TicketDTO> getAll() {
        return mapper.toDtos(repository.findAll());
    }


    public TicketDTO getTicketById(Long id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + id));
        return mapper.toDto(ticket);
    }


    public List<TicketDTO> getTicketsByUserId(Long userId) {
        List<Ticket> tickets = repository.findByUserId(userId);
        return mapper.toDtos(tickets);
    }


    public List<TicketDTO> getTicketsByTrajetId(Long trajetId) {
        List<Ticket> tickets = repository.findByTrajetId(trajetId);
        return mapper.toDtos(tickets);
    }


    @Transactional
    public TicketDTO cancelTicket(Long id) {

        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + id));

        if (ticket.getStatus() == TicketStatus.UTILISE) {
            throw new IllegalStateException("Impossible d'annuler un ticket déjà utilisé");
        }

        ticket.setStatus(TicketStatus.ANNULE);

        if (ticket.getTrajet() != null && ticket.getTrajet().getBus() != null &&
                ticket.getTrajet().getBus().getSpaceAvailable() != null) {
            ticket.getTrajet().getBus().setSpaceAvailable(ticket.getTrajet().getBus().getSpaceAvailable() + 1);
        }

        Ticket updatedTicket = repository.save(ticket);
        return mapper.toDto(updatedTicket);
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

        ticket.setStatus(TicketStatus.UTILISE);

        Ticket updatedTicket = repository.save(ticket);
        return mapper.toDto(updatedTicket);
    }

    @Transactional
    public void deleteTicket(Long id) {

        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé avec l'ID: " + id));

        if (ticket.getStatus() != TicketStatus.ANNULE && ticket.getStatus() != TicketStatus.UTILISE &&
                ticket.getTrajet() != null && ticket.getTrajet().getBus() != null &&
                ticket.getTrajet().getBus().getSpaceAvailable() != null) {
            ticket.getTrajet().getBus().setSpaceAvailable(ticket.getTrajet().getBus().getSpaceAvailable() + 1);
        }

        repository.delete(ticket);
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
}
