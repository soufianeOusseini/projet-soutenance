package com.transi.flex.billings.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private Long companyId;
    private String companyName;
    private Long subscriptionId;
    private Double amount;
    private String status;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private String paymentMethod;
}
