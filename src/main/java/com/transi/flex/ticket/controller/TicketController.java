package com.transi.flex.ticket.controller;

import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.service.TicketService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}/cancel")
    public ResponseEntity
            <TicketDTO> cancelTicket(@PathVariable Long id) {
        try {
            TicketDTO cancelledTicket = service.cancelTicket(id);
            return ResponseEntity.ok(cancelledTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<TicketDTO> confirmReservation(
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

}
