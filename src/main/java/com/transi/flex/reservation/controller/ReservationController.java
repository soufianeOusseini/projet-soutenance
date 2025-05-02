package com.transi.flex.reservation.controller;

import com.transi.flex.reservation.dto.ReservationDTO;
import com.transi.flex.reservation.enums.ReservationStatus;
import com.transi.flex.reservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("reservation")
public class ReservationController {

    private final ReservationService service;

    @GetMapping("/all")
    public List<ReservationDTO> getAll(){
        return service.getAllReservations();
    }

    @PostMapping("")
    public ReservationDTO save(@RequestBody ReservationDTO dto){
        return service.createReservation(dto);
    }

    @GetMapping("/{id}")
    public ReservationDTO getReservation(@PathVariable(name = "id") Long id){
        return service.getReservationById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id){
        service.deleteReservation(id);
    }

}
