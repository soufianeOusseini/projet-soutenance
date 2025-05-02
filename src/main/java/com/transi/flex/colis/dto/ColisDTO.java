package com.transi.flex.colis.dto;

import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.company.model.Company;
import com.transi.flex.trajet.model.Trajet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ColisDTO {
    private Long id;

    private String numero;

    private String expediteur;

    private String destinateur;

    private LocalTime heureEnvoi;

    private Integer nombre;

    private String nature;

    private Double prix;

    private String lieuEnvoi;

    private String lieuReception;

    private Long companyId;

    private Long trajetId;

    private ColisStatus status;
}
