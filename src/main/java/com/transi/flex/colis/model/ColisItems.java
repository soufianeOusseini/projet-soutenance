package com.transi.flex.colis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_COLIS_ITEMS")
public class ColisItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DESCRIPTION",columnDefinition ="TEXT")
    private String description;

    @Column(name = "NOMBRE")
    private Integer nombre;

    @Column(name = "NATURE")
    private String nature;

    @ManyToOne
    @JoinColumn(name = "COLIS_ID")
    @JsonIgnore
    private Colis colis;
}
