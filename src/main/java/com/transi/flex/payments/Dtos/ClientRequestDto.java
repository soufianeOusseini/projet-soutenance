package com.transi.flex.payments.Dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestDto {
    private String phone;
    private int amount;
    private String network; // TMONEY ou MOOV
}
