package com.transi.flex.company.model;


import com.transi.flex.company.dto.CompanyAccountDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface CompanyAccountMapper {

    CompanyAccount toModel(CompanyAccountDTO dto);

    CompanyAccountDTO toDto(CompanyAccount model);

    List<CompanyAccountDTO> toDtos(List<CompanyAccount> models);
}
