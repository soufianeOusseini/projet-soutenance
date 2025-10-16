package com.transi.flex.agency.controller;

import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.agency.dto.AgencyStatsDTO;
import com.transi.flex.agency.enums.AgencyStatus;
import com.transi.flex.agency.service.AgencyService;
import com.transi.flex.company.mapper.CompanyMapperContext;
import com.transi.flex.config.CompanyContextHolder;
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

    @GetMapping("/company")
    public ResponseEntity<List<AgencyDTO>> getAgenciesByCompany() {
        List<AgencyDTO> agencies = agencyService.getAgenciesByCompanyId(CompanyContextHolder.getCurrentId());
        return ResponseEntity.ok(agencies);
    }

    @GetMapping("/{id}")
    public AgencyDTO getAgencyById(@PathVariable Long id) {
        return agencyService.getAgencyById(id);
    }

    @PostMapping
    public ResponseEntity<AgencyDTO> createAgency(@RequestBody AgencyDTO agencyDTO) {
        try {
            AgencyDTO createdAgency = agencyService.createAgency(agencyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAgency);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyDTO> updateAgency(@PathVariable Long id,
                                                  @RequestBody AgencyDTO agencyDTO) {
        try {
            AgencyDTO updatedAgency = agencyService.updateAgency(id, agencyDTO);
            return ResponseEntity.ok(updatedAgency);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgency(@PathVariable Long id) {
        try {
            agencyService.deleteAgency(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AgencyDTO> changeAgencyStatus(@PathVariable Long id,
                                                        @RequestParam AgencyStatus status) {
        try {
            AgencyDTO updatedAgency = agencyService.changeAgencyStatus(id, status);
            return ResponseEntity.ok(updatedAgency);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/company/{companyId}/search")
    public ResponseEntity<List<AgencyDTO>> searchAgencies(@PathVariable Long companyId,
                                                          @RequestParam String keyword) {
        List<AgencyDTO> agencies = agencyService.searchAgencies(companyId, keyword);
        return ResponseEntity.ok(agencies);
    }

    @GetMapping("/company/{companyId}/stats")
    public ResponseEntity<AgencyStatsDTO> getAgencyStats(@PathVariable Long companyId) {
        long activeCount = agencyService.countActiveAgenciesByCompany(companyId);
        List<AgencyDTO> allAgencies = agencyService.getAgenciesByCompanyId(companyId);

        AgencyStatsDTO stats = AgencyStatsDTO.builder()
                .totalAgencies(allAgencies.size())
                .activeAgencies((int) activeCount)
                .inactiveAgencies(allAgencies.size() - (int) activeCount)
                .build();

        return ResponseEntity.ok(stats);
    }

}