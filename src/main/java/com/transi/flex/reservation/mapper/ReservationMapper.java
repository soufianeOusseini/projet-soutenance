package com.transi.flex.reservation.mapper;

import com.transi.flex.reservation.dto.ReservationDTO;
import com.transi.flex.reservation.model.Reservation;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ReservationMapper {

    Reservation dtoModel(ReservationDTO dto);

    ReservationDTO toDto(Reservation model);

    List<ReservationDTO> toDtos(List<Reservation> models);

    List<Reservation> toModels(List<ReservationDTO> dtos);
}
