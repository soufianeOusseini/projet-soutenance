package com.transi.flex.company.model;

import com.transi.flex.agency.model.Agency;
import com.transi.flex.bus.model.Bus;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.company.enums.CompanyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_COMPANY")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ADDRESS", columnDefinition = "TEXT")
    private String address;

    @Column(name = "PHONE")
    private String telephone;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "CITY")
    private String city;

    @Column(name = "REGION")
    private String region;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "EMAIL_ADMIN")
    private String adminEmail;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Column(name = "LOGO_PATH")
    private String logoPath;

    public Company(Long id) {
        this.id = id;
    }

}