package com.transi.flex.tripSchedule.dto;

import com.transi.flex.tripSchedule.enums.ScheduleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TripScheduleDTO {

    private Long trajetId;

    private Long busId;

    private Long driverId;

    private Long agencyId;

    private LocalDate dateDepart;

    private LocalTime heureDepart;

    private Integer nombrePlacesDisponibles;

    private Double prix;

    private ScheduleStatus status = ScheduleStatus.ACTIVE;

}
