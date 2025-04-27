package com.transi.flex.account.controller;
import java.util.List;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.service.UserService;

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
}