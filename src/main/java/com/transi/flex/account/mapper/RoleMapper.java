package com.transi.flex.account.mapper;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.transi.flex.account.dto.RoleDTO;
import com.transi.flex.account.model.Role;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface RoleMapper {

    RoleDTO toDto(Role model);

    Role toModel(RoleDTO dto);

    List<RoleDTO> toDtos(List<Role> models);
}