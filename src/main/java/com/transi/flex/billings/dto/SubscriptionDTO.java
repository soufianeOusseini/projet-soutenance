package com.transi.flex.billings.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long planId;
    private String planName;
    private Double planPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
    private Boolean autoRenew;
    private LocalDate cancelledAt;
}
