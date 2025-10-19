package com.transi.flex.agency.mapper;

import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.agency.model.Agency;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface AgencyMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    AgencyDTO toDto(Agency agency);

    @Mapping(source = "companyId", target = "company.id")
    Agency toModel(AgencyDTO agencyDTO);

    List<AgencyDTO> toDtos(List<Agency> models);
}
