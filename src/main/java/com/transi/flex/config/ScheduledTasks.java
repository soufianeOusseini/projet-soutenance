package com.transi.flex.config;

import com.transi.flex.ticket.service.TicketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
@EnableScheduling
public class ScheduledTasks {

    private final TicketService ticketService;

    /**
     * Tâche programmée pour expirer automatiquement les réservations
     * Exécutée toutes les 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes = 30 * 60 * 1000 ms
    public void expireReservations() {
        try {
            log.info("Début de l'expiration automatique des réservations...");
            ticketService.expireReservations();
            log.info("Expiration automatique des réservations terminée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'expiration automatique des réservations: {}", e.getMessage(), e);
        }
    }

    /**
     * Tâche programmée pour les statistiques quotidiennes
     * Exécutée tous les jours à 23h59
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void dailyStatistics() {
        try {
            log.info("Génération des statistiques quotidiennes...");
            // Ici vous pouvez ajouter la logique pour générer des statistiques
            // Par exemple : nombre de tickets vendus, revenus du jour, etc.
            log.info("Statistiques quotidiennes générées avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la génération des statistiques: {}", e.getMessage(), e);
        }
    }
}