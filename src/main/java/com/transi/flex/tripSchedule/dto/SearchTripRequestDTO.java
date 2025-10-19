package com.transi.flex.tripSchedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class SearchTripRequestDTO {
    private String villeDepart;
    private String villeArrive;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private Integer nombrePassagers;
}