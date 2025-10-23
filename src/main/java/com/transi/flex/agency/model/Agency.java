package com.transi.flex.agency.model;

import com.transi.flex.bus.model.Bus;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.company.model.Company;
import com.transi.flex.agency.enums.AgencyStatus;
import com.transi.flex.company.model.CompanyAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_AGENCY")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CODE", unique = true)
    private String code;

    @Column(name = "ADDRESS", columnDefinition = "TEXT")
    private String address;

    @Column(name = "PHONE")
    private String telephone;

    @Column(name = "CITY")
    private String city;

    @Column(name = "REGION")
    private String region;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "MANAGER_NAME")
    private String managerName;

    @Column(name = "MANAGER_PHONE")
    private String managerPhone;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private AgencyStatus status = AgencyStatus.ACTIVE;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL)
    private Set<Bus> bus = new HashSet<>();

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL)
    private Set<Colis> colis = new HashSet<>();

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CompanyAccount> accounts = new HashSet<>();

    public boolean isActive() {
        return this.status == AgencyStatus.ACTIVE;
    }
}