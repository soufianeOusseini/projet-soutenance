package com.transi.flex.ticket.model;

import com.transi.flex.account.model.User;
import com.transi.flex.reservation.enums.ModePaiement;
import com.transi.flex.reservation.model.Reservation;
import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.trajet.model.Trajet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "T_TICKET")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PRIX", nullable = false)
    private Double prix;

    @Column(name = "NUMBER", unique = true, nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TicketStatus status;

    @Column(name = "DATE")
    private LocalDate date;

    @Column(name = "HEURE_DEPART")
    private LocalTime heureDepart;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "TRAJET_ID", nullable = false)
    private Trajet trajet;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODE_PAIEMENT")
    private ModePaiement modePaiement;

    @OneToOne(mappedBy = "ticket")
    private Reservation reservation;

}
