package com.transi.flex.colis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.company.model.Company;
import com.transi.flex.trajet.model.Trajet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_COLIS")
public class Colis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NUMERO", unique = true, nullable = false)
    private String numero;

    @Column(name = "EXPEDITEUR", nullable = false)
    private String expediteur;

    @Column(name = "DESTINATEUR", nullable = false)
    private String destinateur;

    @Column(name = "HEURE_ENVOI")
    private LocalTime heureEnvoi;

    @Column(name = "PRIX")
    private Double prix;

    @Column(name = "LIEU_ENVOI")
    private String lieuEnvoi;

    @Column(name = "LIEU_RECEPTION")
    private String lieuReception;

    @ManyToOne
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "TRAJET")
    @JsonIgnore
    private Trajet trajet;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ColisStatus status;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColisItems> colisItems = new ArrayList<>();
}