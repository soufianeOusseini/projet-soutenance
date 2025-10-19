package com.transi.flex.account.controller;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transi.flex.account.dto.RegisterRequest;
import com.transi.flex.company.dto.CompanyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.service.UserService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDTO add(@RequestBody UserDTO dto) {
        return service.add(dto);
    }

    @GetMapping("/all")
    public List<UserDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/admin/all")
    public List<UserDTO> getUserAdmin() {
        return service.getUserAdmin();
    }

    @GetMapping("/company")
    public List<UserDTO> getByCompany() {
        return service.getByCompany();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        service.delete(id);
    }

    @PutMapping("/update")
    public void update(@RequestBody UserDTO dto) {
        service.update(dto);
    }

    @PostMapping("upload-profile")
    public void uploadProfile(@RequestParam(name = "profilePath") MultipartFile profile) throws Exception {
        System.out.println("profile" + profile);
        service.uploadProfile(profile);
    }
}