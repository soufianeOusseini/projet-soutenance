package com.transi.flex.tripSchedule.dao;

import com.transi.flex.tripSchedule.model.TripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripScheduleDAO extends JpaRepository<TripSchedule, Long> {
    @Query("SELECT ts FROM TripSchedule ts WHERE ts.dateDepart BETWEEN :startDate AND :endDate AND ts.company.id = :id")
    List<TripSchedule> findByDateRangeAndCompanyId(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate, Long id);

    @Query("SELECT ts FROM TripSchedule ts WHERE ts.dateDepart = :date AND ts.company.id = :id")
    List<TripSchedule> findByDateAndCompanyId(@Param("date") LocalDate date, Long id);

    @Query("SELECT ts FROM TripSchedule ts WHERE ts.bus.id = :busId AND ts.dateDepart = :date")
    List<TripSchedule> findByBusAndDate(@Param("busId") Long busId, @Param("date") LocalDate date);

    @Query("SELECT ts FROM TripSchedule ts WHERE ts.driver.id = :driverId AND ts.dateDepart = :date")
    List<TripSchedule> findByDriverAndDate(@Param("driverId") Long driverId, @Param("date") LocalDate date);

    @Query("SELECT ts FROM TripSchedule ts WHERE ts.company.id = :companyId")
    List<TripSchedule> findByCompany(@Param("companyId") Long companyId);

    List<TripSchedule> findByCompanyId(Long id);

    // Méthodes à ajouter dans TripScheduleDAO

    @Query("SELECT t FROM TripSchedule t WHERE t.trajet.id = :trajetId AND t.dateDepart = :date AND t.company.id = :companyId")
    Optional<TripSchedule> findByTrajetIdAndDate(@Param("trajetId") Long trajetId,
                                                 @Param("date") LocalDate date,
                                                 @Param("companyId") Long companyId);

    // Version simplifiée si vous utilisez CompanyContextHolder dans le service
    @Query("SELECT t FROM TripSchedule t WHERE t.trajet.id = :trajetId AND t.dateDepart = :date")
    Optional<TripSchedule> findByTrajetIdAndDate(@Param("trajetId") Long trajetId,
                                                 @Param("date") LocalDate date);

    // Méthode pour récupérer les planifications avec places disponibles > 0
    @Query("SELECT t FROM TripSchedule t WHERE t.company.id = :companyId AND t.dateDepart BETWEEN :startDate AND :endDate AND t.nombrePlacesDisponibles > 0 ORDER BY t.dateDepart, t.heureDepart")
    List<TripSchedule> findAvailableSchedulesByDateRangeAndCompanyId(@Param("startDate") LocalDate startDate,
                                                                     @Param("endDate") LocalDate endDate,
                                                                     @Param("companyId") Long companyId);
}
