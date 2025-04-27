package com.transi.flex.account.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import com.transi.flex.account.dto.RoleDTO;
import com.transi.flex.account.service.RoleService;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    public RoleDTO add(@RequestBody RoleDTO dto) {
        return service.add(dto);
    }

    @GetMapping("/{id}")
    public RoleDTO getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @GetMapping("/all")
    public List<RoleDTO> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        service.delete(id);
    }

    @PostMapping("/update")
    public void update(@RequestBody RoleDTO dto) {
        service.update(dto);
    }
}