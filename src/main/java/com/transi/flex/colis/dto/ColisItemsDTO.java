package com.transi.flex.colis.dto;

import com.transi.flex.colis.model.Colis;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColisItemsDTO {

    private Long id;

    private String description;

    private Integer nombre;

    private String nature;

}
