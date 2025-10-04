package com.transi.flex.ticket.dto;

import com.transi.flex.reservation.enums.ModePaiement;
import com.transi.flex.ticket.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class TicketDTO {
    private Long id;

    private Double prix;

    private String numero;

    private TicketStatus status;

    private LocalDate date;

    private LocalTime heureDepart;

    private Long userId;

    private Long trajetId;

    private ModePaiement modePaiement;

    private Long reservationId;

    // Informations client
    private String clientNom;
    private String clientPrenom;
    private String clientContact;

    // Type de transaction
    private String typeTransaction; // "ACHAT" ou "RESERVATION"
    private LocalDateTime dateLimitePaiement;

    // Informations supplémentaires pour l'affichage
    private String trajetInfo; // Ex: "Lomé → Kara"
    private String companyName;

    // Méthode utilitaire
    public boolean isReservationExpired() {
        return "RESERVATION".equals(typeTransaction)
                && dateLimitePaiement != null
                && LocalDateTime.now().isAfter(dateLimitePaiement)
                && (status == TicketStatus.RESERVE || status == TicketStatus.EN_ATTENTE);
    }
}
