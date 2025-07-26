package com.transi.flex.driver.dto;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.driver.enums.DriverStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
public class DriverDTO {

    private Long id;

    private String driverLicenseNumber;

    private LocalDate licenseExpiryDate;

    private DriverStatus status;

    private Boolean isAvailable;

    private UserDTO user;
}
