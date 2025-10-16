package com.transi.flex.ticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transi.flex.account.model.User;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.company.model.Company;
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
import java.time.LocalDateTime;
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

    @Column(name = "NUMERO", unique = true, nullable = false)
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
    @JoinColumn(name = "TRAJET", nullable = false)
    private Trajet trajet;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODE_PAIEMENT")
    private ModePaiement modePaiement;

    @OneToOne(mappedBy = "ticket")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "AGENCY_ID")
    @JsonIgnore
    private Agency agency;


    @Column(name = "CLIENT_NOM", nullable = false)
    private String clientNom;

    @Column(name = "CLIENT_PRENOM", nullable = false)
    private String clientPrenom;

    @Column(name = "CLIENT_CONTACT", nullable = false)
    private String clientContact;

    @Column(name = "TYPE_TRANSACTION")
    private String typeTransaction;

    @Column(name = "DATE_LIMITE_PAIEMENT")
    private LocalDateTime dateLimitePaiement;

    public boolean isReservationExpired() {
        return "RESERVATION".equals(typeTransaction)
                && dateLimitePaiement != null
                && LocalDateTime.now().isAfter(dateLimitePaiement)
                && (status == TicketStatus.RESERVE || status == TicketStatus.EN_ATTENTE);
    }
}
