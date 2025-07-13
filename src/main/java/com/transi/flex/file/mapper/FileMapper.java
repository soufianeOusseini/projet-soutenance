package com.transi.flex.file.mapper;

import com.transi.flex.file.dto.FileDTO;
import com.transi.flex.file.dto.FilePayload;
import com.transi.flex.file.model.AppFile;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface FileMapper {

    FileDTO toDto(AppFile model);

    AppFile toModel(FilePayload dto);

    List<FileDTO> toDtos(List<AppFile> models);

}
