package com.transi.flex.bus.mapper;

import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.model.Bus;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface BusMapper {

    Bus toModel(BusDTO dto);

    BusDTO toDto(Bus model);

    List<Bus> toModels(List<BusDTO> dtos);

    List<BusDTO> toDtos(List<Bus> models);
}
