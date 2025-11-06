package com.transi.flex.ticket.dto;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.reservation.enums.ModePaiement;
import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.trajet.dto.TrajetDTO;
import jakarta.persistence.Column;
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

    private TrajetDTO trajet;

    private ModePaiement modePaiement;

    private Long reservationId;

    private String clientNom;
    private String clientPrenom;
    private String clientContact;

    private String typeTransaction;
    private LocalDateTime dateLimitePaiement;

    private String trajetInfo;
    private String companyName;
    private Long agencyId;
    private UserDTO user;
    private Integer seatNumber;

    private String cancellation_reason;

    private String comment;
    public boolean isReservationExpired() {
        return "RESERVATION".equals(typeTransaction)
                && dateLimitePaiement != null
                && LocalDateTime.now().isAfter(dateLimitePaiement)
                && (status == TicketStatus.RESERVE || status == TicketStatus.EN_ATTENTE);
    }
}
