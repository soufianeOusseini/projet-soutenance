package com.transi.flex.trajet.controller;

import com.transi.flex.trajet.dto.TrajetDTO;
import com.transi.flex.trajet.service.TrajetService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("trajet")
public class TrajetController {

    private final TrajetService service;

    @GetMapping("/all")
    public List<TrajetDTO> getAll(){
        return service.getAll();
    }

    @PostMapping("")
    public TrajetDTO save(@RequestBody TrajetDTO dto){
        return service.save(dto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id){
        service.delete(id);
    }

    @GetMapping("/{id}")
    public TrajetDTO getTrajet(@PathVariable(name = "id") Long id){
        return service.getTrajetById(id);
    }
}
