package com.transi.flex.tripSchedule.service;

import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.agency.mapper.AgencyMapper;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.agency.service.AgencyService;
import com.transi.flex.bus.mapper.BusMapper;
import com.transi.flex.bus.service.BusService;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.service.CompanyService;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.driver.mapper.DriverMapper;
import com.transi.flex.driver.service.DriverService;
import com.transi.flex.trajet.mapper.TrajetMapper;
import com.transi.flex.trajet.service.TrajetService;
import com.transi.flex.tripSchedule.dao.TripScheduleDAO;
import com.transi.flex.tripSchedule.dto.ScheduleDTO;
import com.transi.flex.tripSchedule.dto.SearchTripRequestDTO;
import com.transi.flex.tripSchedule.dto.TripScheduleDTO;
import com.transi.flex.tripSchedule.dto.TripSearchResultDTO;
import com.transi.flex.tripSchedule.mapper.TripScheduleMapper;
import com.transi.flex.tripSchedule.model.TripSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripScheduleService {

    private final TripScheduleDAO tripScheduleRepository;

    private final TrajetService trajetService;

    private final BusService busService;

    private final DriverService driverService;

    private final AgencyService agencyService;

    private final TrajetMapper trajetMapper;

    private final DriverMapper driverMapper;

    private final BusMapper busMapper;

    private final AgencyMapper agencyMapper;

    private final TripScheduleMapper mapper;

    private final AgencyRepository agencyRepository;

    private final CompanyService companyService;


    public List<ScheduleDTO> getAllSchedules() {
        Long agencyId = AgencyContextHolder.getCurrentAgencyId();

        if (agencyId != null) {
            // Utilisateur d'agence
            return mapper.toDtos(tripScheduleRepository.findByAgencyId(agencyId));
        } else {
            // Admin compagnie
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId)
                    .stream()
                    .map(Agency::getId)
                    .collect(Collectors.toList());

            List<TripSchedule> schedules = agencyIds.stream()
                    .flatMap(id -> tripScheduleRepository.findByAgencyId(id).stream())
                    .collect(Collectors.toList());

            return mapper.toDtos(schedules);
        }
    }

    public Optional<TripSchedule> getScheduleById(Long id) {
        return tripScheduleRepository.findById(id);
    }



    public List<TripSchedule> getSchedulesByDate(LocalDate date) {
        Long agencyId = AgencyContextHolder.getCurrentAgencyId();

        if (agencyId != null) {
            // Utilisateur d'agence
            return tripScheduleRepository.findByDateAndAgencyId(date, agencyId);
        } else {
            // Admin compagnie
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId)
                    .stream()
                    .map(Agency::getId)
                    .collect(Collectors.toList());

            return agencyIds.stream()
                    .flatMap(id -> tripScheduleRepository.findByDateAndAgencyId(date, id).stream())
                    .collect(Collectors.toList());
        }
    }

    public TripSchedule createSchedule(TripScheduleDTO scheduleDTO) throws Exception {
        // Vérifications de disponibilité
        if (!isBusAvailable(scheduleDTO.getBusId(), scheduleDTO.getDateDepart())) {
            throw new Exception("Bus non disponible pour cette date");
        }

        if (!isDriverAvailable(scheduleDTO.getDriverId(), scheduleDTO.getDateDepart())) {
            throw new Exception("Chauffeur non disponible pour cette date");
        }

        TripSchedule schedule = new TripSchedule();
        schedule.setTrajet(trajetMapper.toModel(trajetService.getTrajetById(scheduleDTO.getTrajetId())));
        schedule.setBus(busMapper.toModel(busService.getBusById(scheduleDTO.getBusId())));
        schedule.setDriver(driverMapper.toModel(driverService.getDriver(scheduleDTO.getDriverId())));
        schedule.setAgency(agencyMapper.toModel(agencyService.getById(AgencyContextHolder.getCurrentAgencyId())));
        schedule.setDateDepart(scheduleDTO.getDateDepart());
        schedule.setHeureDepart(scheduleDTO.getHeureDepart());
        schedule.setNombrePlacesDisponibles(busMapper.toModel(busService.getBusById(scheduleDTO.getBusId())).getCapacity());
        schedule.setPrix(scheduleDTO.getPrix());

        return tripScheduleRepository.save(schedule);
    }

    public TripSchedule updateSchedule(Long id, TripScheduleDTO scheduleDTO) throws Exception {
        Optional<TripSchedule> existingSchedule = tripScheduleRepository.findById(id);
        if (!existingSchedule.isPresent()) {
            throw new Exception("Horaire non trouvé");
        }

        TripSchedule schedule = existingSchedule.get();

        // Vérifications si le bus ou chauffeur change
        if (!schedule.getBus().getId().equals(scheduleDTO.getBusId())) {
            if (!isBusAvailable(scheduleDTO.getBusId(), scheduleDTO.getDateDepart())) {
                throw new Exception("Bus non disponible pour cette date");
            }
            schedule.setBus(busMapper.toModel(busService.getBusById(scheduleDTO.getBusId())));
        }

        if (!schedule.getDriver().getId().equals(scheduleDTO.getDriverId())) {
            if (!isDriverAvailable(scheduleDTO.getDriverId(), scheduleDTO.getDateDepart())) {
                throw new Exception("Chauffeur non disponible pour cette date");
            }
            schedule.setDriver(driverMapper.toModel(driverService.getDriver(scheduleDTO.getDriverId())));
        }

        schedule.setTrajet(trajetMapper.toModel(trajetService.getTrajetById(scheduleDTO.getTrajetId())));
        schedule.setDateDepart(scheduleDTO.getDateDepart());
        schedule.setHeureDepart(scheduleDTO.getHeureDepart());
        schedule.setPrix(scheduleDTO.getPrix());

        return tripScheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        tripScheduleRepository.deleteById(id);
    }

    private boolean isBusAvailable(Long busId, LocalDate date) {
        List<TripSchedule> existingSchedules = tripScheduleRepository.findByBusAndDate(busId, date);
        return existingSchedules.isEmpty();
    }

    private boolean isDriverAvailable(Long driverId, LocalDate date) {
        List<TripSchedule> existingSchedules = tripScheduleRepository.findByDriverAndDate(driverId, date);
        return existingSchedules.isEmpty();
    }

    // Modifier la méthode existante pour ne récupérer que les planifications avec places disponibles
    public List<ScheduleDTO> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        Long agencyId = AgencyContextHolder.getCurrentAgencyId();

        if (agencyId != null) {
            // Utilisateur d'agence
            return mapper.toDtos(tripScheduleRepository.findAvailableSchedulesByDateRangeAndAgencyId(
                    startDate, endDate, agencyId));
        } else {
            // Admin compagnie
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId)
                    .stream()
                    .map(Agency::getId)
                    .collect(Collectors.toList());

            List<TripSchedule> schedules = agencyIds.stream()
                    .flatMap(id -> tripScheduleRepository.findAvailableSchedulesByDateRangeAndAgencyId(
                            startDate, endDate, id).stream())
                    .collect(Collectors.toList());

            return mapper.toDtos(schedules);
        }
    }

    // Nouvelle méthode pour récupérer une planification spécifique
    public Optional<TripSchedule> getScheduleByTrajetAndDate(Long trajetId, LocalDate date) {
        return tripScheduleRepository.findByTrajetIdAndDate(trajetId, date);
    }

//    public List<ScheduleDTO> searchTrips(SearchTripRequestDTO request) {
//        return mapper.toDtos(
//                tripScheduleRepository.findAvailableTrips(
//                        request.getVilleDepart(),
//                        request.getVilleArrive(),
//                        request.getDateDepart(),
//                        request.getHeureDepart(),
//                        request.getNombrePassagers(),
//                        AgencyContextHolder.getCurrentAgencyId()
//                )
//        );
//    }

    public List<TripSearchResultDTO> searchTrips(SearchTripRequestDTO request) throws Exception {
        if (request.getVilleDepart() == null || request.getVilleDepart().isEmpty()) {
            throw new Exception("Ville de départ requise");
        }
        if (request.getVilleArrive() == null || request.getVilleArrive().isEmpty()) {
            throw new Exception("Ville d'arrivée requise");
        }
        if (request.getDateDepart() == null) {
            throw new Exception("Date de départ requise");
        }
        if (request.getHeureDepart() == null) {
            throw new Exception("Heure de départ requise");
        }
        if (request.getNombrePassagers() == null || request.getNombrePassagers() <= 0) {
            throw new Exception("Nombre de passagers invalide");
        }

        List<TripSchedule> trips = tripScheduleRepository.findAvailableTrips(
                request.getVilleDepart(),
                request.getVilleArrive(),
                request.getDateDepart(),
                request.getHeureDepart(),
                request.getNombrePassagers()
        );

        return mapper.toTripSearchDtos(trips);
    }

    public List<String> getDepartureCities() {
        return tripScheduleRepository.findAllDepartureCities();
    }

    public List<String> getArrivalCities(String villeDepart) {
        return tripScheduleRepository.findArrivalCitiesByDeparture(
                villeDepart
        );
    }

}
