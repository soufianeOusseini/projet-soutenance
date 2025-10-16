package com.transi.flex.company.controller;

import com.transi.flex.company.dto.AccountSummaryDTO;
import com.transi.flex.company.dto.CompanyAccountDTO;
import com.transi.flex.company.dto.TransferRequestDTO;
import com.transi.flex.company.enums.AccountStatus;
import com.transi.flex.company.service.CompanyAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/company-accounts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CompanyAccountController {

    private final CompanyAccountService accountService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CompanyAccountDTO>> getAccountsByCompany(@PathVariable Long companyId) {
        List<CompanyAccountDTO> accounts = accountService.getAccountsByAgencyId(companyId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public CompanyAccountDTO getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public ResponseEntity<CompanyAccountDTO> createAccount(@RequestBody CompanyAccountDTO accountDTO) {
        try {
            CompanyAccountDTO createdAccount = accountService.createAccount(accountDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyAccountDTO> updateAccount(@PathVariable Long id,
                                                           @RequestBody CompanyAccountDTO accountDTO) {
        try {
            CompanyAccountDTO updatedAccount = accountService.updateAccount(id, accountDTO);
            return ResponseEntity.ok(updatedAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la suppression du compte: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CompanyAccountDTO> changeAccountStatus(@PathVariable Long id,
                                                                 @RequestParam AccountStatus status) {
        try {
            CompanyAccountDTO updatedAccount = accountService.changeAccountStatus(id, status);
            return ResponseEntity.ok(updatedAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<CompanyAccountDTO> updateBalance(@PathVariable Long id,
                                                           @RequestParam BigDecimal newBalance) {
        try {
            CompanyAccountDTO updatedAccount = accountService.updateBalance(id, newBalance);
            return ResponseEntity.ok(updatedAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(@RequestBody TransferRequestDTO transferRequest) {
        log.info("Transfert de {} du compte {} vers le compte {}",
                transferRequest.getAmount(), transferRequest.getFromAccountId(), transferRequest.getToAccountId());
        try {
            accountService.transferFunds(
                    transferRequest.getFromAccountId(),
                    transferRequest.getToAccountId(),
                    transferRequest.getAmount()
            );
            return ResponseEntity.ok("Transfert effectué avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors du transfert: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/company/{companyId}/summary")
    public ResponseEntity<AccountSummaryDTO> getAccountSummary(@PathVariable Long companyId) {
        log.info("Récupération du résumé des comptes pour la compagnie: {}", companyId);

        BigDecimal totalBalance = accountService.getTotalBalanceByCompany(companyId);
        BigDecimal totalCreditLimit = accountService.getTotalCreditLimitByCompany(companyId);
        long activeAccounts = accountService.countActiveAccountsByCompany(companyId);
        List<CompanyAccountDTO> allAccounts = accountService.getAccountsByAgencyId(companyId);

        AccountSummaryDTO summary = AccountSummaryDTO.builder()
                .totalBalance(totalBalance)
                .totalCreditLimit(totalCreditLimit)
                .activeAccounts((int) activeAccounts)
                .totalAccounts(allAccounts.size())
                .principalAccount(accountService.getPrincipalAccount(companyId))
                .build();

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/company/{companyId}/principal")
    public CompanyAccountDTO getPrincipalAccount(@PathVariable Long companyId) {
        log.info("Récupération du compte principal pour la compagnie: {}", companyId);
        return accountService.getPrincipalAccount(companyId);
    }

}