package com.transi.flex.company.service;

import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.company.dto.CompanyAccountDTO;
import com.transi.flex.company.model.CompanyAccount;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.mapper.CompanyAccountMapper;
import com.transi.flex.company.repository.CompanyAccountRepository;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.company.enums.AccountStatus;
import com.transi.flex.company.enums.AccountType;
import com.transi.flex.config.AgencyContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyAccountService {

    private final CompanyAccountRepository accountRepository;
    private final CompanyRepository companyRepository;
    private final CompanyAccountMapper accountMapper;
    private final AgencyRepository agencyRepository;

    public List<CompanyAccountDTO> getAccountsByAgencyId(Long id) {
        return accountMapper.toDtos(accountRepository.findByAgencyId(id));
    }


    public CompanyAccountDTO getAccountById(Long id) {
        return accountMapper.toDto(accountRepository.findById(id).get());
    }

    public CompanyAccountDTO createAccount(CompanyAccountDTO accountDTO) {

        Agency agency = agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId())
                .orElseThrow(() -> new IllegalArgumentException("Agence non trouvée"));

        CompanyAccount account = convertToEntity(accountDTO);
        account.setAgency(agency);

        CompanyAccount savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    public CompanyAccountDTO updateAccount(Long id, CompanyAccountDTO accountDTO) {
        log.info("Mise à jour du compte ID: {}", id);

        CompanyAccount existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte non trouvé"));

        updateAccountFromDTO(existingAccount, accountDTO);
        CompanyAccount updatedAccount = accountRepository.save(existingAccount);
        return accountMapper.toDto(updatedAccount);
    }

    public void deleteAccount(Long id) {
        log.info("Suppression du compte ID: {}", id);
        CompanyAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte non trouvé"));

        // Vérifier si ce n'est pas le compte principal
        if (account.getType() == AccountType.PRINCIPAL) {
            throw new IllegalArgumentException("Impossible de supprimer le compte principal");
        }

        accountRepository.deleteById(id);
    }

    public CompanyAccountDTO changeAccountStatus(Long id, AccountStatus status) {
        log.info("Changement du statut du compte ID: {} vers {}", id, status);

        CompanyAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte non trouvé"));

        account.setStatus(status);
        CompanyAccount updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional
    public CompanyAccountDTO updateBalance(Long id, BigDecimal newBalance) {
        log.info("Mise à jour du solde du compte ID: {} vers {}", id, newBalance);

        CompanyAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compte non trouvé"));

        account.setBalance(newBalance);
        CompanyAccount updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional
    public void transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        log.info("Transfert de {} du compte {} vers le compte {}", amount, fromAccountId, toAccountId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        CompanyAccount fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Compte source non trouvé"));

        CompanyAccount toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Compte destination non trouvé"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    public BigDecimal getTotalBalanceByCompany(Long companyId) {
        BigDecimal total = accountRepository.getTotalBalanceByCompanyId(companyId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalCreditLimitByCompany(Long companyId) {
        BigDecimal total = accountRepository.getTotalCreditLimitByCompanyId(companyId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public long countActiveAccountsByCompany(Long companyId) {
        return accountRepository.countByAgencyIdAndStatus(companyId, AccountStatus.ACTIVE);
    }

    public CompanyAccountDTO getPrincipalAccount(Long companyId) {
        return accountMapper.toDto(accountRepository.findPrincipalAccountByAgencyId(companyId).get());
    }


    private CompanyAccount convertToEntity(CompanyAccountDTO dto) {
        CompanyAccount account = new CompanyAccount();
        updateAccountFromDTO(account, dto);
        return account;
    }

    private void updateAccountFromDTO(CompanyAccount account, CompanyAccountDTO dto) {
        account.setAccountName(dto.getAccountName());
        account.setBalance(dto.getBalance());
        account.setCreditLimit(dto.getCreditLimit());
        account.setType(dto.getType());
        account.setStatus(dto.getStatus());
        account.setNotes(dto.getNotes());
    }
}