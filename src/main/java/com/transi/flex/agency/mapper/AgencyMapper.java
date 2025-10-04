package com.transi.flex.agency.mapper;

import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.agency.model.Agency;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface AgencyMapper {

    AgencyDTO toDto(Agency model);

    Agency toModel(AgencyDTO to);

    List<AgencyDTO> toDtos(List<Agency> models);
}
