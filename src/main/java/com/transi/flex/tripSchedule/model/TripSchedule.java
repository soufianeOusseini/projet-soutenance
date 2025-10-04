package com.transi.flex.tripSchedule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transi.flex.bus.model.Bus;
import com.transi.flex.company.model.Company;
import com.transi.flex.driver.model.Driver;
import com.transi.flex.trajet.model.Trajet;
import com.transi.flex.tripSchedule.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "T_TRIP_SCHEDULE")
@RequiredArgsConstructor
public class TripSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAJET_ID", nullable = false)
    @JsonIgnore
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUS_ID", nullable = false)
    @JsonIgnore
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID", nullable = false)
    @JsonIgnore
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    @JsonIgnore
    private Company company;

    @Column(name = "DATE_DEPART", nullable = false)
    private LocalDate dateDepart;

    @Column(name = "HEURE_DEPART", nullable = false)
    private LocalTime heureDepart;


    @Column(name = "NOMBRE_PLACES_DISPONIBLES", nullable = false)
    private Integer nombrePlacesDisponibles;

    @Column(name = "PRIX", nullable = false)
    private Double prix;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status = ScheduleStatus.ACTIVE;

}