package com.transi.flex.payments.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaygateCallbackDto {
    private String tx_reference;
    private String identifier;
    private String payment_reference;
    private Double amount;
    private String datetime;
    private String payment_method;
    private String phone_number;

}
