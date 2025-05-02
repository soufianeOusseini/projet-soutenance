package com.transi.flex.colis.controller;

import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.colis.service.ColisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("colis")
public class ColisController {
    private final ColisService service;

    @GetMapping("/all")
    public List<ColisDTO> getAll(){
        return service.getAll();
    }

    @PostMapping("")
    public ColisDTO save(@RequestBody ColisDTO dto){
        return service.save(dto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id){
        service.delete(id);
    }

    @GetMapping("/{id}")
    public ColisDTO getColis(@PathVariable(name = "id") Long id){
        return service.getColisById(id);
    }
}
