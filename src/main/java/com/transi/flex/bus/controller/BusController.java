package com.transi.flex.bus.controller;

import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.service.BusService;
import jakarta.persistence.NamedStoredProcedureQueries;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "bus")
public class BusController {
    private final BusService service;

    @PostMapping
    public BusDTO save(@RequestBody BusDTO dto){
        return service.save(dto);
    }

    @GetMapping("/all")
    public List<BusDTO> getAll(){
        return service.getAll();
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id){
        service.delete(id);
    }

    @GetMapping("/{id}")
    public BusDTO getBus(@PathVariable(name = "id") Long id){
        return service.getBusById(id);
    }

}
