package com.transi.flex.tripSchedule.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.model.Bus;
import com.transi.flex.company.model.Company;
import com.transi.flex.driver.dto.DriverDTO;
import com.transi.flex.driver.model.Driver;
import com.transi.flex.trajet.dto.TrajetDTO;
import com.transi.flex.tripSchedule.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleDTO {

    private Long id;

    private TrajetDTO trajet;

    private BusDTO bus;

    private DriverDTO driver;

    private LocalDate dateDepart;

    private LocalTime heureDepart;

    private Integer nombrePlacesDisponibles;

    private Double prix;

    private ScheduleStatus status = ScheduleStatus.ACTIVE;
}
