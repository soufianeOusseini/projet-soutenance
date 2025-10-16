package com.transi.flex.mobile.dto;

import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.ticket.dto.TicketDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MobileApp {

    private List<CompanyDTO> companies;

    private List<TicketDTO> tickets;

    private List<ColisDTO> colis;

    private List<AgencyDTO> agencies;
}
