package com.transi.flex.tripSchedule.mapper;

import com.transi.flex.tripSchedule.dto.ScheduleDTO;
import com.transi.flex.tripSchedule.dto.TripScheduleDTO;
import com.transi.flex.tripSchedule.dto.TripSearchResultDTO;
import com.transi.flex.tripSchedule.model.TripSchedule;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface TripScheduleMapper {

    TripSchedule toModel(ScheduleDTO dto);
    ScheduleDTO toDto(TripSchedule model);

    List<ScheduleDTO> toDtos(List<TripSchedule> models);

    @Mapping(target = "scheduleId", source = "id")
    @Mapping(target = "placesDisponibles", source = "nombrePlacesDisponibles")
    @Mapping(target = "company", source = "agency.company")
    TripSearchResultDTO toTripSearchDto(TripSchedule tripSchedule);

    /**
     * Méthode de mapping en batch pour les résultats de recherche
     */
    List<TripSearchResultDTO> toTripSearchDtos(List<TripSchedule> tripSchedules);
}
