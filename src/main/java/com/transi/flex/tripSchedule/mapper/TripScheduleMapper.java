package com.transi.flex.tripSchedule.mapper;

import com.transi.flex.tripSchedule.dto.ScheduleDTO;
import com.transi.flex.tripSchedule.dto.TripScheduleDTO;
import com.transi.flex.tripSchedule.model.TripSchedule;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface TripScheduleMapper {

    TripSchedule toModel(ScheduleDTO dto);
    ScheduleDTO toDto(TripSchedule model);

    List<ScheduleDTO> toDtos(List<TripSchedule> models);
}
