package com.transi.flex.reservation.dto;

import com.transi.flex.reservation.enums.ModePaiement;
import com.transi.flex.reservation.enums.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationDTO {

    private Long id;

    private LocalDate date;

    private ReservationStatus status;

    private Integer nombrePlace;

    private Double prix;

    private ModePaiement modePaiement;

    private Long trajetId;

    private Long ticketId;
}
