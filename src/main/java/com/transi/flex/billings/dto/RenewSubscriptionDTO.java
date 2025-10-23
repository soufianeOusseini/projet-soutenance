package com.transi.flex.billings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenewSubscriptionDTO {
    private Long subscriptionId;
    private Long planId; // Optionnel : pour changer de plan
}