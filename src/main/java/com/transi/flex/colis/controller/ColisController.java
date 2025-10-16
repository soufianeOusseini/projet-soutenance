package com.transi.flex.colis.controller;

import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.colis.dto.StatusUpdateDTO;
import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.colis.service.ColisService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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


    @PatchMapping("/{id}/status")
    public ResponseEntity<ColisDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDTO statusUpdate) {
        try {
            ColisStatus newStatus = ColisStatus.valueOf(statusUpdate.getStatus());
            ColisDTO updatedColis = service.updateStatus(id, newStatus);
            return ResponseEntity.ok(updatedColis);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint pour obtenir les transitions de statut possibles
     */
    @GetMapping("/{id}/available-status-transitions")
    public ResponseEntity<List<ColisStatus>> getAvailableStatusTransitions(@PathVariable Long id) {
        List<ColisStatus> availableStatuses = service.getAvailableStatusTransitions(id);
        return ResponseEntity.ok(availableStatuses);
    }
}