package com.transi.flex.trajet.model;

import com.transi.flex.bus.model.Bus;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.reservation.model.Reservation;
import com.transi.flex.ticket.model.Ticket;
import com.transi.flex.trajet.enums.TrajetStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_TRAJET")
public class Trajet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOM")
    private String nom;

    @Column(name = "VILLE_DEPART", nullable = false)
    private String villeDepart;

    @Column(name = "VILLE_ARRIVE", nullable = false)
    private String villeArrive;

    @Column(name = "KM")
    private Double km;

    @Column(name = "HEURE")
    private LocalTime heure;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TrajetStatus status;

    @ManyToOne
    @JoinColumn(name = "BUS_ID")
    private Bus bus;

    @OneToMany(mappedBy = "trajet")
    private Set<Ticket> tickets = new HashSet<>();

    @OneToMany(mappedBy = "trajet")
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(mappedBy = "trajet")
    private Set<Colis> colis = new HashSet<>();

}
