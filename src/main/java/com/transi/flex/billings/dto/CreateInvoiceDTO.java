package com.transi.flex.billings.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceDTO {
    private Long companyId;
    private Long subscriptionId;
    private Double amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
}