package com.transi.flex.billings.service;


import com.transi.flex.billings.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduledTasks {

    private final SubscriptionService subscriptionService;

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
    @Scheduled(cron = "0 0 9 * * ?")
    public void notifyExpiringSubscriptions() {
        log.info("Starting scheduled task: Notifying expiring subscriptions");
        try {
            var expiringSubscriptions = subscriptionService.getExpiringSubscriptions(7);

            expiringSubscriptions.forEach(subscription -> {
                log.info("Subscription {} for company {} expires on {}",
                        subscription.getId(),
                        subscription.getCompanyName(),
                        subscription.getEndDate());

                // TODO: Envoyer email/SMS de notification
                // notificationService.sendExpirationNotification(subscription);
            });

            log.info("Found {} subscriptions expiring in the next 7 days", expiringSubscriptions.size());
        } catch (Exception e) {
            log.error("Error notifying expiring subscriptions", e);
        }
    }
}

