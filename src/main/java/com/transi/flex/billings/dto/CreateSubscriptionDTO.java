package com.transi.flex.billings.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionDTO {
    private Long companyId;
    private Long planId;
    private LocalDate startDate;
    private Boolean autoRenew;
}