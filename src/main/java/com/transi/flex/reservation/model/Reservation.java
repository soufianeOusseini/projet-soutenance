package com.transi.flex.reservation.model;

import com.transi.flex.reservation.enums.ModePaiement;
import com.transi.flex.reservation.enums.ReservationStatus;
import com.transi.flex.ticket.model.Ticket;
import com.transi.flex.trajet.model.Trajet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_RESERVATION")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATE")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ReservationStatus status;

    @Column(name = "NOMBRE_PLACE")
    private Integer nombrePlace;

    @Column(name = "PRIX")
    private Double prix;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODE_PAIEMENT")
    private ModePaiement modePaiement;

    @ManyToOne
    @JoinColumn(name = "trajet", nullable = false)
    private Trajet trajet;

    @OneToOne
    @JoinColumn(name = "ticket")
    private Ticket ticket;
}