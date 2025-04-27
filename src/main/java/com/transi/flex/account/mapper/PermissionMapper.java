package com.transi.flex.account.mapper;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.transi.flex.account.dto.PermissionDTO;
import com.transi.flex.account.model.Permission;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface PermissionMapper {

    Permission toModel(PermissionDTO dto);

    PermissionDTO toDto(Permission permission);

    List<PermissionDTO> toDtos(List<Permission> permissions);
}