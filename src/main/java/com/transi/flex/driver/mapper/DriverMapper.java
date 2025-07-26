package com.transi.flex.driver.mapper;

import com.transi.flex.driver.dto.DriverDTO;
import com.transi.flex.driver.model.Driver;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface DriverMapper {

    Driver toModel(DriverDTO dto);

    DriverDTO toDto(Driver model);

    List<DriverDTO> toDtos(List<Driver> models);
}
