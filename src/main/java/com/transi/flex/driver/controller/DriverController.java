package com.transi.flex.driver.controller;

import com.transi.flex.driver.dto.DriverDTO;
import com.transi.flex.driver.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("driver")
public class DriverController {
    
    private final DriverService service;

    @GetMapping("/all")
    public List<DriverDTO> getAll(){
        return service.getAll();
    }

    @PostMapping("")
    public DriverDTO save(@RequestBody DriverDTO dto){
        return service.save(dto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id){
        service.delete(id);
    }

    @GetMapping("/{id}")
    public DriverDTO getDriver(@PathVariable(name = "id") Long id){
        return service.getDriver(id);
    }
    
}
