package com.transi.flex.tripSchedule.dao;

import com.transi.flex.tripSchedule.model.TripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripScheduleDAO extends JpaRepository<TripSchedule, Long> {
    @Query("SELECT ts FROM TripSchedule ts WHERE ts.dateDepart BETWEEN :startDate AND :endDate AND ts.agency.id = :id")
    List<TripSchedule> findByDateRangeAndAgencyId(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate, Long id);

    @Query("SELECT ts FROM TripSchedule ts WHERE ts.dateDepart = :date AND ts.agency.id = :id")
    List<TripSchedule> findByDateAndAgencyId(@Param("date") LocalDate date, Long id);

    @Query("SELECT ts FROM TripSchedule ts WHERE ts.bus.id = :busId AND ts.dateDepart = :date")
    List<TripSchedule> findByBusAndDate(@Param("busId") Long busId, @Param("date") LocalDate date);

    @Query("SELECT ts FROM TripSchedule ts WHERE ts.driver.id = :driverId AND ts.dateDepart = :date")
    List<TripSchedule> findByDriverAndDate(@Param("driverId") Long driverId, @Param("date") LocalDate date);

    List<TripSchedule> findByAgencyId(Long id);

    // Méthodes à ajouter dans TripScheduleDAO

    @Query("SELECT t FROM TripSchedule t WHERE t.trajet.id = :trajetId AND t.dateDepart = :date AND t.agency.id = :agencyId")
    Optional<TripSchedule> findByTrajetIdAndDate(@Param("trajetId") Long trajetId,
                                                 @Param("date") LocalDate date,
                                                 @Param("agencyId") Long agencyId);

    // Version simplifiée si vous utilisez CompanyContextHolder dans le service
    @Query("SELECT t FROM TripSchedule t WHERE t.trajet.id = :trajetId AND t.dateDepart = :date")
    Optional<TripSchedule> findByTrajetIdAndDate(@Param("trajetId") Long trajetId,
                                                 @Param("date") LocalDate date);

    // Méthode pour récupérer les planifications avec places disponibles > 0
    @Query("SELECT t FROM TripSchedule t WHERE t.agency.id = :agencyId AND t.dateDepart BETWEEN :startDate AND :endDate AND t.nombrePlacesDisponibles > 0 ORDER BY t.dateDepart, t.heureDepart")
    List<TripSchedule> findAvailableSchedulesByDateRangeAndAgencyId(@Param("startDate") LocalDate startDate,
                                                                     @Param("endDate") LocalDate endDate,
                                                                     @Param("agencyId") Long agencyId);

    @Query("""
    SELECT ts FROM TripSchedule ts
     JOIN FETCH ts.agency a
        JOIN FETCH a.company c
    WHERE ts.trajet.villeDepart = :villeDepart
    AND ts.trajet.villeArrive = :villeArrive
    AND ts.dateDepart = :dateDepart
    AND ts.heureDepart >= :heureDepart
    AND ts.nombrePlacesDisponibles >= :nombrePassagers
    ORDER BY ts.heureDepart
""")
    List<TripSchedule> findAvailableTrips(
            @Param("villeDepart") String villeDepart,
            @Param("villeArrive") String villeArrive,
            @Param("dateDepart") LocalDate dateDepart,
            @Param("heureDepart") LocalTime heureDepart,
            @Param("nombrePassagers") Integer nombrePassagers
    );

    @Query("""
        SELECT DISTINCT t.villeDepart FROM Trajet t
        ORDER BY t.villeDepart ASC
    """)
    List<String> findAllDepartureCities();

    @Query("""
        SELECT DISTINCT t.villeArrive FROM Trajet t
        WHERE t.villeDepart = :villeDepart
        ORDER BY t.villeArrive ASC
    """)
    List<String> findArrivalCitiesByDeparture(
            @Param("villeDepart") String villeDepart
    );

}
