package com.transi.flex.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
    private Long id;
    private String name;
    private String address;
    private String telephone;
    private String postalCode;
    private String city;
    private String region;
    private String email;
    private String adminFirstName;
    private String adminLastName;
    private String adminPhone;
    private String adminEmail;
}