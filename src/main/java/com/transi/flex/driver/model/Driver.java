package com.transi.flex.driver.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transi.flex.account.model.User;
import com.transi.flex.company.model.Company;
import com.transi.flex.driver.enums.DriverStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_DRIVER")
@RequiredArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DRIVER_LICENSE_NUMBER", unique = true, nullable = false)
    private String driverLicenseNumber;

    @Column(name = "LICENSE_EXPIRY_DATE")
    private LocalDate licenseExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private DriverStatus status;

    @Column(name = "IS_AVAILABLE")
    private Boolean isAvailable;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "COMPANY_ID")
    @JsonIgnore
    private Company company;

    public boolean isLicenseValid() {
        return licenseExpiryDate != null && licenseExpiryDate.isAfter(LocalDate.now());
    }

    public boolean isActiveDriver() {
        return DriverStatus.ACTIVE.equals(status) && isLicenseValid();
    }

    public String getFullName() {
        return user != null ? user.getFullName() : "";
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

}