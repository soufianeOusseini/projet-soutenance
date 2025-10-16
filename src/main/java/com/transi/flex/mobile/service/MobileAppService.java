package com.transi.flex.mobile.service;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.service.UserService;
import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.agency.service.AgencyService;
import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.colis.repository.ColisRepository;
import com.transi.flex.colis.service.ColisService;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.company.service.CompanyService;
import com.transi.flex.mobile.dto.MobileApp;
import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.repository.TicketRepository;
import com.transi.flex.ticket.service.TicketService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MobileAppService {

    private final CompanyService companyService;

    private final CompanyRepository companyRepository;

    private final TicketService ticketService;

    private final TicketRepository ticketRepository;

    private final ColisRepository colisRepository;

    private final ColisService colisService;

    private final UserService userService;

    private final AgencyService agencyService;


    public MobileApp getMobileAppState(){
        UserDTO user = userService.getCurrentUser();

        MobileApp mobileApp = new MobileApp();
        List<AgencyDTO> agencies = agencyService.findAll();
        List<CompanyDTO> companies = companyService.getAll();
        List<TicketDTO> tickets = ticketService.getByUser(user.getId());
        List<ColisDTO> colis = colisService.getByUser(user.getId());
        mobileApp.setAgencies(agencies);
        mobileApp.setCompanies(companies);
        mobileApp.setColis(colis);
        mobileApp.setTickets(tickets);
        return mobileApp;
    }
}
