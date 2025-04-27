package com.transi.flex.company.mapper;

import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.model.Company;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface CompanyMapper {

    CompanyDTO toDto(Company model);

    Company toModel(CompanyDTO dto);

    List<CompanyDTO> toDtos(List<Company> models);

    List<Company> toModels(List<CompanyDTO> dtos);
}