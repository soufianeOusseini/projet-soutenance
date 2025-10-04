package com.transi.flex.company.dto;

import java.math.BigDecimal;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public  class AccountSummaryDTO {
    private BigDecimal totalBalance;
    private BigDecimal totalCreditLimit;
    private int activeAccounts;
    private int totalAccounts;
    private CompanyAccountDTO principalAccount;
}
