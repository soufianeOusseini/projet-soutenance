package com.transi.flex.bus.dto;

import com.transi.flex.company.model.Company;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusDTO {

    private Long id;

    private String plaque;

    private String model;

    private Integer capacity;

    private String number;

    private String image;

    private String type;

    private String status;

    private Integer spaceAvailable;

    private Long companyId;
}
