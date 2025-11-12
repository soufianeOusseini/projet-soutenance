package com.transi.flex.dashboard.service;

import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.bus.repository.BusRepository;
import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.colis.repository.ColisRepository;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.dashboard.dto.DashboardDTO;
import com.transi.flex.dashboard.dto.SuperDashboardStatsDTO;
import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.ticket.repository.TicketRepository;
import com.transi.flex.trajet.repository.TrajetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final ColisRepository colisRepository;
    private final TrajetRepository trajetRepository;
    private final BusRepository busRepository;
    private final AgencyRepository agencyRepository;
    private final CompanyRepository companyRepository;

    public DashboardDTO getDashboardData() {
        Long agencyId = AgencyContextHolder.getCurrentAgencyId();
        boolean isCompanyLevel = (agencyId == null);

        return DashboardDTO.builder()
                .isCompanyLevel(isCompanyLevel)
                .statistiquesGenerales(getStatistiquesGenerales(agencyId, isCompanyLevel))
                .revenus(getRevenus(agencyId, isCompanyLevel))
                .colisStatistics(getColisStatistics(agencyId, isCompanyLevel))
                .trajetRepartition(getTrajetRepartition(agencyId, isCompanyLevel))
                .activitesRecentes(getActivitesRecentes(agencyId, isCompanyLevel))
                .build();
    }

    private DashboardDTO.StatistiquesGeneralesDTO getStatistiquesGenerales(Long agencyId, boolean isCompanyLevel) {
        if (isCompanyLevel) {
            return getCompanyStatistiques();
        }
        return getAgencyStatistiques(agencyId);
    }

    private DashboardDTO.StatistiquesGeneralesDTO getAgencyStatistiques(Long agencyId) {
        long totalTrips = trajetRepository.countByAgencyId(agencyId);
        long totalColis = colisRepository.countByAgencyId(agencyId);

        long totalTickets = ticketRepository.findByAgencyId(agencyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE)
                .count();

        long totalReservations = ticketRepository.findByAgencyId(agencyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVE)
                .count();

        long totalPassengers = totalTickets + totalReservations;

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);

        long ticketsThisMonth = ticketRepository.findByAgencyId(agencyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE
                        && ticket.getDate() != null
                        && (ticket.getDate().getYear() > startOfMonth.getYear()
                        || (ticket.getDate().getYear() == startOfMonth.getYear()
                        && ticket.getDate().getMonthValue() >= startOfMonth.getMonthValue())))
                .count();

        long ticketsLastMonth = ticketRepository.findByAgencyId(agencyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE
                        && ticket.getDate() != null
                        && ticket.getDate().getYear() == startOfMonth.minusMonths(1).getYear()
                        && ticket.getDate().getMonthValue() == startOfMonth.minusMonths(1).getMonthValue())
                .count();

        double ticketsPercentage = ticketsLastMonth > 0
                ? ((ticketsThisMonth - ticketsLastMonth) / (double) ticketsLastMonth) * 100
                : 0;

        double totalEarnings = ticketRepository.findByAgencyId(agencyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE)
                .mapToDouble(ticket -> ticket.getPrix() != null ? ticket.getPrix() : 0)
                .sum();

        return DashboardDTO.StatistiquesGeneralesDTO.builder()
                .totalTrips(totalTrips)
                .percentageTripsChange(ticketsPercentage)
                .totalPassengers(totalPassengers)
                .percentagePassengersChange(15.0)
                .totalEarnings(totalEarnings)
                .percentageEarningsChange(-18.0)
                .totalTickets(totalTickets)
                .percentageTicketsChange(8.5)
                .totalReservations(totalReservations)
                .percentageReservationsChange(12.3)
                .totalColis(totalColis)
                .percentageColisChange(12.5)
                .totalAgencies(null) // Pas applicable pour une agence
                .build();
    }

    private DashboardDTO.StatistiquesGeneralesDTO getCompanyStatistiques() {
        Long companyId = CompanyContextHolder.getCurrentId();

        // Récupérer toutes les agences de la compagnie
        List<Long> agencyIds = agencyRepository.findByCompanyId(companyId).stream()
                .map(agency -> agency.getId())
                .collect(Collectors.toList());

        // Statistiques agrégées de toutes les agences
        long totalTrips = agencyIds.stream()
                .mapToLong(id -> trajetRepository.countByAgencyId(id))
                .sum();

        long totalColis = agencyIds.stream()
                .mapToLong(id -> colisRepository.countByAgencyId(id))
                .sum();

        long totalTickets = agencyIds.stream()
                .mapToLong(id -> ticketRepository.findByAgencyId(id).stream()
                        .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE)
                        .count())
                .sum();

        long totalReservations = agencyIds.stream()
                .mapToLong(id -> ticketRepository.findByAgencyId(id).stream()
                        .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVE)
                        .count())
                .sum();

        long totalPassengers = totalTickets + totalReservations;

        double totalEarnings = agencyIds.stream()
                .mapToDouble(id -> ticketRepository.findByAgencyId(id).stream()
                        .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE)
                        .mapToDouble(ticket -> ticket.getPrix() != null ? ticket.getPrix() : 0)
                        .sum())
                .sum();

        // Calcul du pourcentage de changement (à améliorer avec vraies données)
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);

        long ticketsThisMonth = agencyIds.stream()
                .mapToLong(id -> ticketRepository.findByAgencyId(id).stream()
                        .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE
                                && ticket.getDate() != null
                                && (ticket.getDate().getYear() > startOfMonth.getYear()
                                || (ticket.getDate().getYear() == startOfMonth.getYear()
                                && ticket.getDate().getMonthValue() >= startOfMonth.getMonthValue())))
                        .count())
                .sum();

        long ticketsLastMonth = agencyIds.stream()
                .mapToLong(id -> ticketRepository.findByAgencyId(id).stream()
                        .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE
                                && ticket.getDate() != null
                                && ticket.getDate().getYear() == startOfMonth.minusMonths(1).getYear()
                                && ticket.getDate().getMonthValue() == startOfMonth.minusMonths(1).getMonthValue())
                        .count())
                .sum();

        double ticketsPercentage = ticketsLastMonth > 0
                ? ((ticketsThisMonth - ticketsLastMonth) / (double) ticketsLastMonth) * 100
                : 0;

        return DashboardDTO.StatistiquesGeneralesDTO.builder()
                .totalTrips(totalTrips)
                .percentageTripsChange(ticketsPercentage)
                .totalPassengers(totalPassengers)
                .percentagePassengersChange(15.0)
                .totalEarnings(totalEarnings)
                .percentageEarningsChange(-18.0)
                .totalTickets(totalTickets)
                .percentageTicketsChange(8.5)
                .totalReservations(totalReservations)
                .percentageReservationsChange(12.3)
                .totalColis(totalColis)
                .percentageColisChange(12.5)
                .totalAgencies((long) agencyIds.size()) // Nombre d'agences de la compagnie
                .build();
    }

    private DashboardDTO.RevenusDTO getRevenus(Long agencyId, boolean isCompanyLevel) {
        LocalDate today = LocalDate.now();

        if (isCompanyLevel) {
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId).stream()
                    .map(agency -> agency.getId())
                    .collect(Collectors.toList());

            double revenusToday = agencyIds.stream()
                    .mapToDouble(id -> ticketRepository.findByAgencyId(id).stream()
                            .filter(t -> t.getStatus() == TicketStatus.PAYE
                                    && t.getDate() != null
                                    && t.getDate().equals(today))
                            .mapToDouble(t -> t.getPrix() != null ? t.getPrix() : 0)
                            .sum())
                    .sum();

            double revenusThisMonth = agencyIds.stream()
                    .mapToDouble(id -> ticketRepository.findByAgencyId(id).stream()
                            .filter(t -> t.getStatus() == TicketStatus.PAYE
                                    && t.getDate() != null
                                    && t.getDate().getYear() == today.getYear()
                                    && t.getDate().getMonthValue() == today.getMonthValue())
                            .mapToDouble(t -> t.getPrix() != null ? t.getPrix() : 0)
                            .sum())
                    .sum();

            List<DashboardDTO.RevenueByDayDTO> revenuesByDay = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                final LocalDate checkDate = date;
                double amount = agencyIds.stream()
                        .mapToDouble(id -> ticketRepository.findByAgencyId(id).stream()
                                .filter(t -> t.getStatus() == TicketStatus.PAYE
                                        && t.getDate() != null
                                        && t.getDate().equals(checkDate))
                                .mapToDouble(t -> t.getPrix() != null ? t.getPrix() : 0)
                                .sum())
                        .sum();
                revenuesByDay.add(DashboardDTO.RevenueByDayDTO.builder()
                        .day(date.getDayOfWeek().toString().substring(0, 1))
                        .amount(amount)
                        .build());
            }

            return DashboardDTO.RevenusDTO.builder()
                    .revenusToday(revenusToday)
                    .revenusThisMonth(revenusThisMonth)
                    .percentageMonthChange(21.68)
                    .revenuesByDay(revenuesByDay)
                    .build();
        }

        // Code existant pour une agence spécifique
        double revenusToday = ticketRepository.findByAgencyId(agencyId).stream()
                .filter(t -> t.getStatus() == TicketStatus.PAYE
                        && t.getDate() != null
                        && t.getDate().equals(today))
                .mapToDouble(t -> t.getPrix() != null ? t.getPrix() : 0)
                .sum();

        double revenusThisMonth = ticketRepository.findByAgencyId(agencyId).stream()
                .filter(t -> t.getStatus() == TicketStatus.PAYE
                        && t.getDate() != null
                        && t.getDate().getYear() == today.getYear()
                        && t.getDate().getMonthValue() == today.getMonthValue())
                .mapToDouble(t -> t.getPrix() != null ? t.getPrix() : 0)
                .sum();

        List<DashboardDTO.RevenueByDayDTO> revenuesByDay = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            final LocalDate checkDate = date;
            double amount = ticketRepository.findByAgencyId(agencyId).stream()
                    .filter(t -> t.getStatus() == TicketStatus.PAYE
                            && t.getDate() != null
                            && t.getDate().equals(checkDate))
                    .mapToDouble(t -> t.getPrix() != null ? t.getPrix() : 0)
                    .sum();
            revenuesByDay.add(DashboardDTO.RevenueByDayDTO.builder()
                    .day(date.getDayOfWeek().toString().substring(0, 1))
                    .amount(amount)
                    .build());
        }

        return DashboardDTO.RevenusDTO.builder()
                .revenusToday(revenusToday)
                .revenusThisMonth(revenusThisMonth)
                .percentageMonthChange(21.68)
                .revenuesByDay(revenuesByDay)
                .build();
    }

    private DashboardDTO.ColisStatisticsDTO getColisStatistics(Long agencyId, boolean isCompanyLevel) {
        if (isCompanyLevel) {
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId).stream()
                    .map(agency -> agency.getId())
                    .collect(Collectors.toList());

            long totalColis = agencyIds.stream()
                    .mapToLong(id -> colisRepository.countByAgencyId(id))
                    .sum();

            long colisDelivered = agencyIds.stream()
                    .mapToLong(id -> colisRepository.countByStatusAndAgencyId(ColisStatus.LIVRE, id))
                    .sum();

            long colisPending = agencyIds.stream()
                    .mapToLong(id -> colisRepository.countByStatusAndAgencyId(ColisStatus.EN_ATTENTE, id))
                    .sum();

            long colisInTransit = agencyIds.stream()
                    .mapToLong(id -> colisRepository.countByStatusAndAgencyId(ColisStatus.EN_TRANSIT, id))
                    .sum();

            double deliveryRate = totalColis > 0 ? (colisDelivered / (double) totalColis) * 100 : 0;

            return DashboardDTO.ColisStatisticsDTO.builder()
                    .totalColis(totalColis)
                    .colisDelivered(colisDelivered)
                    .colisPending(colisPending)
                    .colisInTransit(colisInTransit)
                    .percentageDelivered(deliveryRate)
                    .deliveryRate(deliveryRate)
                    .build();
        }

        long totalColis = colisRepository.countByAgencyId(agencyId);
        long colisDelivered = colisRepository.countByStatusAndAgencyId(ColisStatus.LIVRE, agencyId);
        long colisPending = colisRepository.countByStatusAndAgencyId(ColisStatus.EN_ATTENTE, agencyId);
        long colisInTransit = colisRepository.countByStatusAndAgencyId(ColisStatus.EN_TRANSIT, agencyId);

        double deliveryRate = totalColis > 0 ? (colisDelivered / (double) totalColis) * 100 : 0;

        return DashboardDTO.ColisStatisticsDTO.builder()
                .totalColis(totalColis)
                .colisDelivered(colisDelivered)
                .colisPending(colisPending)
                .colisInTransit(colisInTransit)
                .percentageDelivered(deliveryRate)
                .deliveryRate(deliveryRate)
                .build();
    }

    private List<DashboardDTO.TrajetRepartitionDTO> getTrajetRepartition(Long agencyId, boolean isCompanyLevel) {
        if (isCompanyLevel) {
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId).stream()
                    .map(agency -> agency.getId())
                    .collect(Collectors.toList());

            return agencyIds.stream()
                    .flatMap(id -> trajetRepository.findByAgencyId(id).stream())
                    .collect(Collectors.groupingBy(
                            trajet -> trajet.getVilleDepart() + " → " + trajet.getVilleArrive(),
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(4)
                    .map(entry -> DashboardDTO.TrajetRepartitionDTO.builder()
                            .route(entry.getKey())
                            .count(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
        }

        return trajetRepository.findByAgencyId(agencyId).stream()
                .collect(Collectors.groupingBy(
                        trajet -> trajet.getVilleDepart() + " → " + trajet.getVilleArrive(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(4)
                .map(entry -> DashboardDTO.TrajetRepartitionDTO.builder()
                        .route(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<DashboardDTO.ActiviteRecenteDTO> getActivitesRecentes(Long agencyId, boolean isCompanyLevel) {
        List<DashboardDTO.ActiviteRecenteDTO> activites = new ArrayList<>();

        if (isCompanyLevel) {
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId).stream()
                    .map(agency -> agency.getId())
                    .collect(Collectors.toList());

            agencyIds.stream()
                    .flatMap(id -> ticketRepository.findByAgencyId(id).stream())
                    .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE)
                    .sorted((a, b) -> {
                        LocalDateTime dateA = a.getDate() != null ? a.getDate().atStartOfDay() : LocalDateTime.MIN;
                        LocalDateTime dateB = b.getDate() != null ? b.getDate().atStartOfDay() : LocalDateTime.MIN;
                        return dateB.compareTo(dateA);
                    })
                    .limit(5)
                    .forEach(ticket -> activites.add(DashboardDTO.ActiviteRecenteDTO.builder()
                            .type("TICKET")
                            .description("Ticket #" + ticket.getNumero() + " vendu")
                            .timeAgo(calculateTimeAgo(ticket.getDate()))
                            .build()));
        } else {
            ticketRepository.findByAgencyId(agencyId).stream()
                    .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE)
                    .sorted((a, b) -> {
                        LocalDateTime dateA = a.getDate() != null ? a.getDate().atStartOfDay() : LocalDateTime.MIN;
                        LocalDateTime dateB = b.getDate() != null ? b.getDate().atStartOfDay() : LocalDateTime.MIN;
                        return dateB.compareTo(dateA);
                    })
                    .limit(5)
                    .forEach(ticket -> activites.add(DashboardDTO.ActiviteRecenteDTO.builder()
                            .type("TICKET")
                            .description("Ticket #" + ticket.getNumero() + " vendu")
                            .timeAgo(calculateTimeAgo(ticket.getDate()))
                            .build()));
        }

        return activites;
    }

    private String calculateTimeAgo(LocalDate date) {
        if (date == null) {
            return "date inconnue";
        }

        LocalDateTime dateTime = date.atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        long seconds = ChronoUnit.SECONDS.between(dateTime, now);
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (seconds < 60) {
            return "à l'instant";
        } else if (minutes < 60) {
            return "il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (hours < 24) {
            return "il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        } else if (days < 7) {
            return "il y a " + days + " jour" + (days > 1 ? "s" : "");
        } else if (days < 30) {
            long weeks = days / 7;
            return "il y a " + weeks + " semaine" + (weeks > 1 ? "s" : "");
        } else {
            long months = days / 30;
            return "il y a " + months + " mois";
        }
    }

    public SuperDashboardStatsDTO getDashboardStats() {
        SuperDashboardStatsDTO stats = new SuperDashboardStatsDTO();

        stats.setTotalCompanies(companyRepository.count());
        stats.setTotalAgencies(agencyRepository.count());
        stats.setTotalTickets(ticketRepository.count());

        Double totalSales = ticketRepository.sumTotalSales();
        stats.setTotalSales(totalSales != null ? totalSales : 0.0);

        stats.setMonthlySales(getMonthlySales());

        return stats;
    }

    private List<SuperDashboardStatsDTO.MonthlySalesDTO> getMonthlySales() {
        List<SuperDashboardStatsDTO.MonthlySalesDTO> monthlySales = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            int year = monthDate.getYear();
            int month = monthDate.getMonthValue();

            Double sales = ticketRepository.sumSalesByYearAndMonth(year, month);

            String monthName = monthDate.getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.FRENCH)
                    .substring(0, 3);

            monthlySales.add(new SuperDashboardStatsDTO.MonthlySalesDTO(
                    monthName,
                    sales != null ? sales : 0.0
            ));
        }

        return monthlySales;
    }
}