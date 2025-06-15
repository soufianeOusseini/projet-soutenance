package com.transi.flex.bus.model;

import com.transi.flex.bus.enums.BusStatus;
import com.transi.flex.company.model.Company;
import com.transi.flex.trajet.model.Trajet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "T_BUS")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PLAQUE")
    private String plaque;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "CAPACITY")
    private Integer capacity;

    @Column(name = "NUMERO")
    private Integer number;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private BusStatus status;

    @Column(name = "SPACE_AVAILABLE")
    private Integer spaceAvailable;

    @ManyToOne
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "bus")
    private Set<Trajet> trajets = new HashSet<>();
}
