package com.transi.flex.tripSchedule.service;

import com.transi.flex.bus.mapper.BusMapper;
import com.transi.flex.bus.service.BusService;
import com.transi.flex.company.mapper.CompanyMapper;
import com.transi.flex.company.service.CompanyService;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.driver.mapper.DriverMapper;
import com.transi.flex.driver.service.DriverService;
import com.transi.flex.trajet.mapper.TrajetMapper;
import com.transi.flex.trajet.service.TrajetService;
import com.transi.flex.tripSchedule.dao.TripScheduleDAO;
import com.transi.flex.tripSchedule.dto.ScheduleDTO;
import com.transi.flex.tripSchedule.dto.TripScheduleDTO;
import com.transi.flex.tripSchedule.mapper.TripScheduleMapper;
import com.transi.flex.tripSchedule.model.TripSchedule;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripScheduleService {

    private final TripScheduleDAO tripScheduleRepository;

    private final TrajetService trajetService;

    private final BusService busService;

    private final DriverService driverService;

    private final CompanyService companyService;

    private final TrajetMapper trajetMapper;

    private final DriverMapper driverMapper;

    private final BusMapper busMapper;

    private final CompanyMapper companyMapper;

    private final TripScheduleMapper mapper;


    public List<ScheduleDTO> getAllSchedules() {
        return mapper.toDtos(tripScheduleRepository.findByCompanyId(CompanyContextHolder.getCurrentId()));
    }

    public Optional<TripSchedule> getScheduleById(Long id) {
        return tripScheduleRepository.findById(id);
    }

    public List<ScheduleDTO> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return mapper.toDtos(tripScheduleRepository.findByDateRangeAndCompanyId(startDate, endDate, CompanyContextHolder.getCurrentId()));
    }

    public List<TripSchedule> getSchedulesByDate(LocalDate date) {
        return tripScheduleRepository.findByDateAndCompanyId(date, CompanyContextHolder.getCurrentId());
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
        schedule.setCompany(companyMapper.toModel(companyService.getById(CompanyContextHolder.getCurrentId())));
        schedule.setDateDepart(scheduleDTO.getDateDepart());
        schedule.setHeureDepart(scheduleDTO.getHeureDepart());
        schedule.setNombrePlacesTotales(scheduleDTO.getNombrePlacesTotales());
        schedule.setNombrePlacesDisponibles(scheduleDTO.getNombrePlacesTotales());
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
        schedule.setNombrePlacesTotales(scheduleDTO.getNombrePlacesTotales());
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
}
