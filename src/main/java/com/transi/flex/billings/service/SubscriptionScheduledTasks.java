package com.transi.flex.billings.service;


import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.dto.UserSummary;
import com.transi.flex.account.service.UserService;
import com.transi.flex.billings.service.SubscriptionService;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.mailing.dto.EmailRequest;
import com.transi.flex.mailing.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduledTasks {

    private final SubscriptionService subscriptionService;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final UserService userService;
    private final CompanyRepository companyRepository;

    @Value("${mail.noreplay.from}")
    private String fromEmail;

    @Value("${mail.noreplay.sender}")
    private String senderEmail;

    @Value("${app-url}")
    private String appUrl;
    /**
     * Traite les abonnements expirés chaque jour à 1h du matin
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processExpiredSubscriptions() {
        log.info("Starting scheduled task: Processing expired subscriptions");
        try {
            subscriptionService.processExpiredSubscriptions();
            log.info("Successfully processed expired subscriptions");
        } catch (Exception e) {
            log.error("Error processing expired subscriptions", e);
        }
    }

    /**
     * Envoie des notifications pour les abonnements expirant dans 7 jours
     * S'exécute chaque jour à 9h du matin
     */
    @Scheduled(cron = "0 55 22 * * ?")
    public void notifyExpiringSubscriptions() {
        log.info("Starting scheduled task: Notifying expiring subscriptions");
        try {
            var expiringSubscriptions = subscriptionService.getExpiringSubscriptions(7);

            expiringSubscriptions.forEach(subscription -> {
                try {
                    Company company = companyRepository.findById(subscription.getCompanyId())
                            .orElse(null);

                    if (company == null || company.getAdminEmail() == null) {
                        log.warn("Company {} not found or has no admin email", subscription.getCompanyId());
                        return;
                    }

                    UserSummary user = userService.getByEmail(company.getAdminEmail());

                    if (user == null) {
                        log.warn("User not found for email: {}", company.getAdminEmail());
                        return;
                    }

                    // Calculer les jours restants
                    LocalDate today = LocalDate.now();
                    LocalDate endDate = subscription.getEndDate();
                    long daysRemaining = ChronoUnit.DAYS.between(today, endDate);
                    boolean isExpired = daysRemaining < 0;

                    // Préparer le sujet dynamique
                    String subject = isExpired
                            ? "🔴 URGENT : Votre abonnement a expiré - " + company.getName()
                            : "⚠️ Votre abonnement expire dans " + daysRemaining + " jour(s) - " + company.getName();

                    Context context = new Context();
                    context.setVariable("user", user);
                    context.setVariable("companyName", company.getName());
                    context.setVariable("planName", subscription.getPlanName());
                    context.setVariable("planPrice", subscription.getPlanPrice());
                    context.setVariable("startDate", subscription.getStartDate());
                    context.setVariable("expirationDate", subscription.getEndDate());
                    context.setVariable("daysRemaining", Math.abs(daysRemaining));
                    context.setVariable("isExpired", isExpired);
                    context.setVariable("loginUrl", appUrl + "auth/login");

                    // Informations footer
                    context.setVariable("companyAddress", company.getAddress());
                    context.setVariable("companyPhone", company.getTelephone());
                    context.setVariable("companyEmail", company.getEmail());

                    // Logo (optionnel)
                    context.setVariable("hasLogo", company.getLogoPath() != null);
                    if (company.getLogoPath() != null) {
                        // Convertir le logo en base64 si nécessaire
                        context.setVariable("logoBase64", "data:image/png;base64," + convertLogoToBase64(company.getLogoPath()));
                    }

                    var emailRequest = EmailRequest.builder()
                            .lang(Locale.FRENCH.toLanguageTag())
                            .from(fromEmail)
                            .senderName(senderEmail)
                            .to(new String[] { user.getEmail() })
                            .subject(subject)
                            .build();

                    log.info("Sending expiration notification for subscription {} to {}",
                            subscription.getId(), user.getEmail());

                    emailService.sendExpirationNotification(context, "subscription", emailRequest, subscription);

                    log.info("Email sent successfully to {} for subscription {}",
                            user.getEmail(), subscription.getId());

                } catch (Exception e) {
                    log.error("Error sending notification for subscription {}: {}",
                            subscription.getId(), e.getMessage(), e);
                }
            });

            log.info("Processed {} subscriptions expiring in the next 7 days", expiringSubscriptions.size());
        } catch (Exception e) {
            log.error("Error notifying expiring subscriptions", e);
        }
    }

    // Méthode helper pour convertir le logo en base64 (si nécessaire)
    private String convertLogoToBase64(String logoPath) {
        // Implémenter selon votre système de stockage
        // Exemple simplifié
        return "";
    }
}

