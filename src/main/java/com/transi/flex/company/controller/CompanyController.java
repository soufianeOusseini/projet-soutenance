package com.transi.flex.company.controller;

import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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


    @PutMapping
    public ResponseEntity<CompanyDTO> update(@RequestBody CompanyDTO dto) {
        return ResponseEntity.ok(service.update(dto));
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
}