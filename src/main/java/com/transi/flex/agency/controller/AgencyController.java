package com.transi.flex.agency.controller;

import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.agency.enums.AgencyStatus;
import com.transi.flex.agency.service.AgencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agencies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AgencyController {

    private final AgencyService agencyService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AgencyDTO>> getAgenciesByCompany(@PathVariable Long companyId) {
        log.info("Récupération des agences pour la compagnie: {}", companyId);
        List<AgencyDTO> agencies = agencyService.getAgenciesByCompanyId(companyId);
        return ResponseEntity.ok(agencies);
    }

    @GetMapping("/{id}")
    public AgencyDTO getAgencyById(@PathVariable Long id) {
        log.info("Récupération de l'agence: {}", id);
        return agencyService.getAgencyById(id);
    }

    @PostMapping
    public ResponseEntity<AgencyDTO> createAgency(@RequestBody AgencyDTO agencyDTO) {
        log.info("Création d'une nouvelle agence: {}", agencyDTO.getName());
        try {
            AgencyDTO createdAgency = agencyService.createAgency(agencyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAgency);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création de l'agence: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyDTO> updateAgency(@PathVariable Long id,
                                                  @RequestBody AgencyDTO agencyDTO) {
        log.info("Mise à jour de l'agence: {}", id);
        try {
            AgencyDTO updatedAgency = agencyService.updateAgency(id, agencyDTO);
            return ResponseEntity.ok(updatedAgency);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la mise à jour de l'agence: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgency(@PathVariable Long id) {
        log.info("Suppression de l'agence: {}", id);
        try {
            agencyService.deleteAgency(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la suppression de l'agence: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AgencyDTO> changeAgencyStatus(@PathVariable Long id,
                                                        @RequestParam AgencyStatus status) {
        log.info("Changement du statut de l'agence {} vers: {}", id, status);
        try {
            AgencyDTO updatedAgency = agencyService.changeAgencyStatus(id, status);
            return ResponseEntity.ok(updatedAgency);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors du changement de statut: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/company/{companyId}/search")
    public ResponseEntity<List<AgencyDTO>> searchAgencies(@PathVariable Long companyId,
                                                          @RequestParam String keyword) {
        log.info("Recherche d'agences pour la compagnie {} avec: {}", companyId, keyword);
        List<AgencyDTO> agencies = agencyService.searchAgencies(companyId, keyword);
        return ResponseEntity.ok(agencies);
    }

    @GetMapping("/company/{companyId}/stats")
    public ResponseEntity<AgencyStatsDTO> getAgencyStats(@PathVariable Long companyId) {
        log.info("Récupération des statistiques d'agences pour la compagnie: {}", companyId);
        long activeCount = agencyService.countActiveAgenciesByCompany(companyId);
        List<AgencyDTO> allAgencies = agencyService.getAgenciesByCompanyId(companyId);

        AgencyStatsDTO stats = AgencyStatsDTO.builder()
                .totalAgencies(allAgencies.size())
                .activeAgencies((int) activeCount)
                .inactiveAgencies(allAgencies.size() - (int) activeCount)
                .build();

        return ResponseEntity.ok(stats);
    }

    // DTO pour les statistiques
    @lombok.Data
    @lombok.Builder
    public static class AgencyStatsDTO {
        private int totalAgencies;
        private int activeAgencies;
        private int inactiveAgencies;
    }
}