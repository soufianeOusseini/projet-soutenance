package com.transi.flex.payments.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckResponseDto {
    private String tx_reference;
    private String payment_reference;
    private String datetime;
    private String identifier;
    private String payment_method;
    private String phone_number;
    private int status;
}
