package com.transi.flex.dashboard.service;

import com.transi.flex.bus.repository.BusRepository;
import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.colis.repository.ColisRepository;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.dashboard.dto.DashboardDTO;
import com.transi.flex.ticket.enums.TicketStatus;
import com.transi.flex.ticket.repository.TicketRepository;
import com.transi.flex.trajet.repository.TrajetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final ColisRepository colisRepository;
    private final TrajetRepository trajetRepository;
    private final BusRepository busRepository;

    public DashboardDTO getDashboardData() {
        return DashboardDTO.builder()
                .statistiquesGenerales(getStatistiquesGenerales())
                .revenus(getRevenus())
                .colisStatistics(getColisStatistics())
                .trajetRepartition(getTrajetRepartition())
                .activitesRecentes(getActivitesRecentes())
                .build();
    }

    private DashboardDTO.StatistiquesGeneralesDTO getStatistiquesGenerales() {
        Long companyId = CompanyContextHolder.getCurrentId();
        long totalTrips = trajetRepository.countByCompanyId(companyId);
        long totalColis = colisRepository.countByCompanyId(companyId);

        long totalTickets = ticketRepository.findByCompanyId(companyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE)
                .count();

        long totalReservations = ticketRepository.findByCompanyId(companyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVE)
                .count();

        long totalPassengers = totalTickets + totalReservations;

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);

        long ticketsThisMonth = ticketRepository.findByCompanyId(companyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE
                        && ticket.getDate() != null
                        && (ticket.getDate().getYear() > startOfMonth.getYear()
                        || (ticket.getDate().getYear() == startOfMonth.getYear()
                        && ticket.getDate().getMonthValue() >= startOfMonth.getMonthValue())))
                .count();

        long ticketsLastMonth = ticketRepository.findByCompanyId(companyId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAYE
                        && ticket.getDate() != null
                        && ticket.getDate().getYear() == startOfMonth.minusMonths(1).getYear()
                        && ticket.getDate().getMonthValue() == startOfMonth.minusMonths(1).getMonthValue())
                .count();

        double ticketsPercentage = ticketsLastMonth > 0
                ? ((ticketsThisMonth - ticketsLastMonth) / (double) ticketsLastMonth) * 100
                : 0;

        double totalEarnings = ticketRepository.findByCompanyId(companyId).stream()
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
                .build();
    }

    private DashboardDTO.RevenusDTO getRevenus() {
        Long companyId = CompanyContextHolder.getCurrentId();
        LocalDate today = LocalDate.now();

        double revenusToday = ticketRepository.findByCompanyId(companyId).stream()
                .filter(t -> t.getStatus() == TicketStatus.PAYE
                        && t.getDate() != null
                        && t.getDate().equals(today))
                .mapToDouble(t -> t.getPrix() != null ? t.getPrix() : 0)
                .sum();

        double revenusThisMonth = ticketRepository.findByCompanyId(companyId).stream()
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
            double amount = ticketRepository.findByCompanyId(companyId).stream()
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

    private DashboardDTO.ColisStatisticsDTO getColisStatistics() {
        Long companyId = CompanyContextHolder.getCurrentId();
        long totalColis = colisRepository.countByCompanyId(companyId);
        long colisDelivered = colisRepository.countByStatusAndCompanyId(ColisStatus.LIVRE, companyId);
        long colisPending = colisRepository.countByStatusAndCompanyId(ColisStatus.EN_ATTENTE, companyId);
        long colisInTransit = colisRepository.countByStatusAndCompanyId(ColisStatus.EN_TRANSIT, companyId);

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

    private List<DashboardDTO.TrajetRepartitionDTO> getTrajetRepartition() {
        Long companyId = CompanyContextHolder.getCurrentId();
        return trajetRepository.findByCompanyId(companyId).stream()
                .collect(Collectors.groupingBy(
                        trajet -> trajet.getVilleDepart() + " ? " + trajet.getVilleArrive(),
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

    private List<DashboardDTO.ActiviteRecenteDTO> getActivitesRecentes() {
        Long companyId = CompanyContextHolder.getCurrentId();
        List<DashboardDTO.ActiviteRecenteDTO> activites = new ArrayList<>();

        ticketRepository.findByCompanyId(companyId).stream()
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
}