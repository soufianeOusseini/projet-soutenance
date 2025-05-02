package com.transi.flex.ticket.controller;

import com.transi.flex.ticket.dto.TicketDTO;
import com.transi.flex.ticket.service.TicketService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("ticket")
public class TicketController {
    private final TicketService service;

    @GetMapping("/all")
    public List<TicketDTO> getAll(){
        return service.getAll();
    }

    @PostMapping("")
    public TicketDTO save(@RequestBody TicketDTO dto){
        return service.save(dto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id){
        service.deleteTicket(id);
    }

    @GetMapping("/{id}")
    public TicketDTO getTicket(@PathVariable(name = "id") Long id){
        return service.getTicketById(id);
    }
}
