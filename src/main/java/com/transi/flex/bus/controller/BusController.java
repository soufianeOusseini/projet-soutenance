package com.transi.flex.bus.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.service.BusService;
import com.transi.flex.company.dto.CompanyDTO;
import jakarta.persistence.NamedStoredProcedureQueries;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(value = "bus")
public class BusController {
    private final BusService service;

//    @PostMapping
//    public BusDTO save(@RequestBody BusDTO dto){
//        return service.save(dto);
//    }

    @PostMapping
    public ResponseEntity<BusDTO> save(@RequestParam(name = "image", required = false) MultipartFile image,
                                             @RequestParam("bus") String busStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        BusDTO bus = mapper.readValue(busStr, BusDTO.class);
        return ResponseEntity.ok(service.save(bus, Optional.ofNullable(image)));
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
