package com.transi.flex.payments.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositResponseDto {
    private String tx_reference;
    private int status;
}
