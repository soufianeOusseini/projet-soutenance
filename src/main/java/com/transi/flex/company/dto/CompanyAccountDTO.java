package com.transi.flex.company.dto;

import com.transi.flex.company.enums.AccountStatus;
import com.transi.flex.company.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyAccountDTO {
    private Long id;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private AccountType type;
    private AccountStatus status;
    private String notes;
    private Long agencyId;
    private String agencyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}