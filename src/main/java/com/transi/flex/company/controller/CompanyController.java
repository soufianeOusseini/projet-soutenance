package com.transi.flex.company.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompanyDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<CompanyDTO> add(@RequestBody CompanyDTO dto) {
        return new ResponseEntity<>(service.add(dto), HttpStatus.CREATED);
    }

    @PostMapping
    public  ResponseEntity<CompanyDTO> update(@RequestParam(name = "logoPath", required = false) MultipartFile logoPath,
                         @RequestParam("company") String companyStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CompanyDTO company = mapper.readValue(companyStr, CompanyDTO.class);
        return ResponseEntity.ok(service.update(company, Optional.ofNullable(logoPath)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/logo/{id}")
    public ResponseEntity<String> getCompanyLogo(@PathVariable Long id) {
        return ResponseEntity.ok("Logo placeholder");
    }

    @GetMapping("/change-status/{id}")
    public void changeStatus(@PathVariable(name = "id") Long id){
        service.changeStatus(id);
    }

    @PostMapping("/logo")
    public  void updateLogo(@RequestParam(name = "logoPath", required = false) MultipartFile logoPath,
                                              @RequestParam("id") Long id) throws Exception {
        service.updateLogo(id, Optional.ofNullable(logoPath));
    }
}