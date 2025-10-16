package com.transi.flex.trajet.dto;

import com.transi.flex.bus.model.Bus;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.reservation.model.Reservation;
import com.transi.flex.ticket.model.Ticket;
import com.transi.flex.trajet.enums.TrajetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TrajetDTO {
    private Long id;

    private String nom;

    private String villeDepart;

    private String villeArrive;

    private Double km;

    private LocalTime heure;

    private TrajetStatus status;

    private Bus bus;

    private Set<Ticket> tickets = new HashSet<>();

    private Set<Reservation> reservations = new HashSet<>();

    private Set<Colis> colis = new HashSet<>();

    private Long agencyId;

    private Double amount;
}
