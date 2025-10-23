package com.transi.flex.billings.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionPlanDTO {
    private String name;
    private Double price;
    private Integer durationInDays;
    private String description;
}
