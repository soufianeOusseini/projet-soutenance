package com.transi.flex.payments.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequestDto {
    private String auth_token;
    private String phone_number;
    private int amount;
    private String description;
    private String identifier;
    private String network;
}
