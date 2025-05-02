package com.transi.flex.ticket.dto;

import com.transi.flex.reservation.enums.ModePaiement;
import com.transi.flex.ticket.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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
}
