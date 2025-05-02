package com.transi.flex.trajet.mapper;

import com.transi.flex.trajet.dto.TrajetDTO;
import com.transi.flex.trajet.model.Trajet;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface TrajetMapper {

    Trajet toModel(TrajetDTO dto);

    TrajetDTO toDto(Trajet model);

    List<Trajet> toModels(List<TrajetDTO> dtos);

    List<TrajetDTO> toDtos(List<Trajet> models);
}
