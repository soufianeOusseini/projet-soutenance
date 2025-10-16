package com.transi.flex.ticket.mapper;

import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.model.Ticket;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface TicketMapper {
    Ticket toModel(TicketDTO dto);

    @Mapping(target = "trajetId", source = "trajet.id")
    @Mapping(target = "trajetInfo", expression = "java(model.getTrajet().getVilleDepart() + \" -> \" + model.getTrajet().getVilleArrive())")
    TicketDTO toDto(Ticket model);


    List<TicketDTO> toDtos(List<Ticket> models);

    List<Ticket> toModels(List<TicketDTO> dtos);
}
