package com.transi.flex.payments.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckTransactionDto {
    private String tx_reference;
    private String auth_token;
}
