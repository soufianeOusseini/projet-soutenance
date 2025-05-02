package com.transi.flex.colis.mapper;

import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.colis.model.Colis;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ColisMapper {

    Colis toModel(ColisDTO dto);

    ColisDTO toDto(Colis model);

    List<ColisDTO> toDtos(List<Colis> models);
}
