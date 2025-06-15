package com.transi.flex.colis.mapper;

import com.transi.flex.colis.dto.ColisItemsDTO;
import com.transi.flex.colis.model.ColisItems;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ColisItemsMapper {

    ColisItems toModel(ColisItemsDTO dto);

    ColisItemsDTO toDto(ColisItems model);

    List<ColisItemsDTO> toDtos(List<ColisItems> models);
}
