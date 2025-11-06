package com.transi.flex.colis.dto;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.model.User;
import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.colis.model.ColisItems;
import com.transi.flex.company.model.Company;
import com.transi.flex.trajet.model.Trajet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ColisDTO {
    private Long id;

    private String numero;

    private String expediteur;

    private String destinateur;

    private LocalTime heureEnvoi;

    private Double prix;

    private String lieuEnvoi;

    private String lieuReception;

    private Long agencyId;

    private Long trajetId;

    private ColisStatus status;

    private List<ColisItems> colisItems = new ArrayList<>();

    private UserDTO user;

    private AgencyDTO agency;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdBy;
}
