package com.transi.flex.billings.service;


import com.transi.flex.billings.dao.SubscriptionDAO;
import com.transi.flex.billings.dao.SubscriptionPlanDAO;
import com.transi.flex.billings.dto.CreateSubscriptionDTO;
import com.transi.flex.billings.dto.RenewSubscriptionDTO;
import com.transi.flex.billings.dto.SubscriptionDTO;
import com.transi.flex.billings.mapper.SubscriptionMapper;
import com.transi.flex.billings.model.Subscription;
import com.transi.flex.billings.model.SubscriptionPlan;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionDAO subscriptionRepository;
    private final SubscriptionPlanDAO planRepository;
    private final CompanyRepository companyRepository;
    private final SubscriptionMapper mapper;
    private final InvoiceService invoiceService;

    public SubscriptionDTO createSubscription(CreateSubscriptionDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        subscriptionRepository.findActiveSubscriptionByCompanyId(
                dto.getCompanyId(),
                LocalDate.now()
        ).ifPresent(sub -> {
            throw new RuntimeException("Company already has an active subscription");
        });

        Subscription subscription = new Subscription();
        subscription.setCompany(company);
        subscription.setPlan(plan);
        subscription.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now());
        subscription.setEndDate(subscription.getStartDate().plusDays(plan.getDurationInDays()));
        subscription.setActive(true);
        subscription.setAutoRenew(dto.getAutoRenew() != null ? dto.getAutoRenew() : false);

        Subscription saved = subscriptionRepository.save(subscription);

        // Générer automatiquement la facture
        invoiceService.generateInvoiceForSubscription(saved);

        return mapper.toDto(saved);
    }

    public SubscriptionDTO renewSubscription(RenewSubscriptionDTO dto) {
        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        SubscriptionPlan newPlan = dto.getPlanId() != null
                ? planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"))
                : subscription.getPlan();

        // Créer un nouvel abonnement
        Subscription newSubscription = new Subscription();
        newSubscription.setCompany(subscription.getCompany());
        newSubscription.setPlan(newPlan);
        newSubscription.setStartDate(subscription.getEndDate().plusDays(1));
        newSubscription.setEndDate(newSubscription.getStartDate().plusDays(newPlan.getDurationInDays()));
        newSubscription.setActive(true);
        newSubscription.setAutoRenew(subscription.getAutoRenew());

        // Désactiver l'ancien abonnement
        subscription.setActive(false);
        subscriptionRepository.save(subscription);

        Subscription saved = subscriptionRepository.save(newSubscription);

        // Générer la facture pour le renouvellement
        invoiceService.generateInvoiceForSubscription(saved);

        return mapper.toDto(saved);
    }

    public void cancelSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setActive(false);
        subscription.setCancelledAt(LocalDate.now());
        subscription.setAutoRenew(false);
        subscriptionRepository.save(subscription);
    }

    public SubscriptionDTO getActiveSubscriptionByCompany(Long companyId) {
        return subscriptionRepository.findActiveSubscriptionByCompanyId(companyId, LocalDate.now())
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("No active subscription found"));
    }

    public List<SubscriptionDTO> getSubscriptionsByCompany(Long companyId) {
        return subscriptionRepository.findByCompanyId(companyId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SubscriptionDTO> getExpiringSubscriptions(int daysBeforeExpiry) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysBeforeExpiry);

        return subscriptionRepository.findSubscriptionsExpiringBetween(startDate, endDate)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public void processExpiredSubscriptions() {
        List<Subscription> expired = subscriptionRepository.findByActiveTrueAndEndDateBefore(LocalDate.now());

        for (Subscription subscription : expired) {
            if (subscription.getAutoRenew()) {
                try {
                    RenewSubscriptionDTO renewDTO = new RenewSubscriptionDTO();
                    renewDTO.setSubscriptionId(subscription.getId());
                    renewSubscription(renewDTO);
                } catch (Exception e) {
                    // Log error et désactiver l'abonnement
                    subscription.setActive(false);
                    subscriptionRepository.save(subscription);
                }
            } else {
                subscription.setActive(false);
                subscriptionRepository.save(subscription);
            }
        }
    }

    public SubscriptionDTO updateAutoRenew(Long subscriptionId, Boolean autoRenew) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!subscription.getActive()) {
            throw new RuntimeException("Cannot update auto-renew for inactive subscription");
        }

        subscription.setAutoRenew(autoRenew);
        Subscription saved = subscriptionRepository.save(subscription);

        return mapper.toDto(saved);
    }
}
