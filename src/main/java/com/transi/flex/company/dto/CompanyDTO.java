package com.transi.flex.company.dto;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.dto.UserSummary;
import com.transi.flex.company.enums.CompanyStatus;
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
    private Long adminId;
    private String adminFirstName;
    private String adminLastName;
    private String adminPhone;
    private String adminEmail;
    private UserSummary admin;
    private CompanyStatus status;
    private String logoPath;

}