package com.transi.flex.tripSchedule.dto;

import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.trajet.dto.TrajetDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TripSearchResultDTO {
    private Long scheduleId;
    private TrajetDTO trajet;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private Integer placesDisponibles;
    private Double prix;
    private BusDTO bus;
    private CompanyDTO company;
    private AgencyDTO agency;
}
