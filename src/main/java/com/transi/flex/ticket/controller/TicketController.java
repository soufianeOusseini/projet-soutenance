package com.transi.flex.ticket.controller;

import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.ticket.service.TicketService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("ticket")
public class TicketController {
    private final TicketService service;

    @GetMapping("/all")
    public List<TicketDTO> getAll(){
        return service.getAll();
    }

    @PostMapping("")
    public TicketDTO save(@RequestBody TicketDTO dto){
        return service.save(dto);
    }

//    @DeleteMapping("/delete/{id}")
//    public void delete(@PathVariable(name = "id") Long id){
//        service.deleteTicket(id);
//    }

    @GetMapping("/{id}")
    public TicketDTO getTicket(@PathVariable(name = "id") Long id){
        return service.getTicketById(id);
    }

    // Endpoint à ajouter dans TicketController

//    @PutMapping("/{id}/cancel")
//    public ResponseEntity
//            <TicketDTO> cancelTicket(@PathVariable Long id) {
//        try {
//            TicketDTO cancelledTicket = service.cancelTicket(id);
//            return ResponseEntity.ok(cancelledTicket);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        } catch (IllegalStateException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<TicketDTO> confirmReservationWeb(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String modePaiement = payload.get("modePaiement");
        TicketDTO confirmedTicket = service.confirmReservation(id, modePaiement);
        return ResponseEntity.ok(confirmedTicket);
    }

    @PutMapping("/{id}/use")
    public ResponseEntity<TicketDTO> useTicket(@PathVariable Long id) {
        TicketDTO usedTicket = service.useTicket(id);
        return ResponseEntity.ok(usedTicket);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadTicketPdf(@PathVariable Long id) {
        byte[] pdfBytes = service.generateTicketPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ticket-" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PutMapping("/validate/{numero}")
    public ResponseEntity<TicketDTO> validateTicket(@PathVariable String numero) {
        TicketDTO validatedTicket = service.validateTicketByNumero(numero);
        return ResponseEntity.ok(validatedTicket);
    }

    @PostMapping("/expire-reservations")
    public ResponseEntity<Void> expireReservations() {
        service.expireReservations();
        return ResponseEntity.ok().build();
    }

    /**
     * Récupérer tous les tickets de l'utilisateur connecté
     */
    @GetMapping("/user")
    public ResponseEntity<List<TicketDTO>> getTicketsByUser() {
        try {
            List<TicketDTO> tickets = service.getTicketsByUser();
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Créer un nouveau ticket (réservation ou achat)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@RequestBody TicketDTO ticketDTO) {
        try {
            TicketDTO savedTicket = service.save(ticketDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la création du ticket"));
        }
    }

    /**
     * Confirmer une réservation (payer)
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmReservation(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String modePaiement = request.get("modePaiement");
            TicketDTO confirmedTicket = service.confirmReservation(id, modePaiement);
            return ResponseEntity.ok(confirmedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la confirmation"));
        }
    }

    /**
     * Annuler un ticket
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTicket(@PathVariable Long id,@RequestParam(required = false) String cancellationReason,
                                          @RequestParam(required = false) String comment) {
        try {
            TicketDTO cancelledTicket = service.cancelTicket(id, cancellationReason, comment);
            return ResponseEntity.ok(cancelledTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'annulation"));
        }
    }

    /**
     * Récupérer les tickets par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketDTO>> getTicketsByStatus(@PathVariable String status) {
        try {
            TicketStatus ticketStatus = TicketStatus.valueOf(status.toUpperCase());
            List<TicketDTO> tickets = service.getTicketsByStatus(ticketStatus);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/occupied-seats/{trajetId}/{date}")
    public ResponseEntity<List<Integer>> getOccupiedSeats(
            @PathVariable Long trajetId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Integer> occupiedSeats = service.getOccupiedSeats(trajetId, date);
        return ResponseEntity.ok(occupiedSeats);
    }
}
