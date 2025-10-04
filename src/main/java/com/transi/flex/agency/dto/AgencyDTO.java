package com.transi.flex.agency.dto;

import com.transi.flex.agency.enums.AgencyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyDTO {
    private Long id;
    private String name;
    private String code;
    private String address;
    private String telephone;
    private String city;
    private String region;
    private String email;
    private String managerName;
    private String managerPhone;
    private AgencyStatus status;
    private Long companyId;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}