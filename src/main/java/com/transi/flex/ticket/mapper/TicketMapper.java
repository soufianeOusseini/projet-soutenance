package com.transi.flex.ticket.mapper;

import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.model.Ticket;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface TicketMapper {
    Ticket toModel(TicketDTO dto);

    TicketDTO toDto(Ticket model);

    List<TicketDTO> toDtos(List<Ticket> models);

    List<Ticket> toModels(List<TicketDTO> dtos);
}
