package com.transi.flex.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDTO {
    private StatistiquesGeneralesDTO statistiquesGenerales;

    private RevenusDTO revenus;

    private ColisStatisticsDTO colisStatistics;

    private List<TrajetPlanifieDTO> trajetsPlanifies;

    private List<TrajetRepartitionDTO> trajetRepartition;

    private List<ActiviteRecenteDTO> activitesRecentes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StatistiquesGeneralesDTO {
        private Long totalTrips;
        private Double percentageTripsChange;
        private Long totalPassengers;
        private Double percentagePassengersChange;
        private Double totalEarnings;
        private Double percentageEarningsChange;
        private Long totalTickets;
        private Double percentageTicketsChange;
        private Long totalReservations;
        private Double percentageReservationsChange;
        private Long totalColis;
        private Double percentageColisChange;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RevenusDTO {
        private Double revenusToday;
        private Double revenusThisMonth;
        private Double percentageMonthChange;
        private List<RevenueByDayDTO> revenuesByDay;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RevenueByDayDTO {
        private String day;
        private Double amount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ColisStatisticsDTO {
        private Long totalColis;
        private Long colisDelivered;
        private Long colisPending;
        private Long colisInTransit;
        private Double percentageDelivered;
        private Double deliveryRate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TrajetPlanifieDTO {
        private String trajet;
        private String heureDepart;
        private String busNumber;
        private String driver;
        private String status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TrajetRepartitionDTO {
        private String route;
        private Long count;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ActiviteRecenteDTO {
        private String type;
        private String description;
        private String timeAgo;
    }
}