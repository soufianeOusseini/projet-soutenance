package com.transi.flex.billings.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayInvoiceDTO {
    private String paymentMethod;
    private LocalDate paymentDate;
}