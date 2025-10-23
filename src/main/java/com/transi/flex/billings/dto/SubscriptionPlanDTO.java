package com.transi.flex.billings.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer durationInDays;
    private String description;
    private Boolean active;
}