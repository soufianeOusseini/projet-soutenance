package com.transi.flex.account.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import com.transi.flex.account.dto.PermissionDTO;
import com.transi.flex.account.service.PermissionService;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService service;

    @GetMapping("/all")
    public List<PermissionDTO> findAllPermission() {
        return service.findAll();
    }

    @PutMapping("/add/{roleId}/{permissionName}")
    public void addPermissionToRole(@PathVariable(name = "roleId") Long roleId,
                                    @PathVariable(name = "permissionName") String permissionName) {
        service.addPermissionToRole(roleId, permissionName);
    }

    @PutMapping("/delete/{roleId}/{permissionName}")
    public void removePermissionFromRole(@PathVariable(name = "roleId") Long roleId,
                                         @PathVariable(name = "permissionName") String permissionName) {
        service.removePermissionFromRole(roleId, permissionName);
    }

    @PutMapping("/add-multiple/{roleId}/{permissionNames}")
    public void addPermissionsToRole(@PathVariable(name = "roleId") Long roleId,
                                     @PathVariable(name = "permissionNames") List<String> permissionNames) {
        service.addPermissionsToRole(roleId, permissionNames);
    }

    @PutMapping("/delete-multiple/{roleId}/{permissionNames}")
    public void removePermissionsFromRole(@PathVariable(name = "roleId") Long roleId,
                                          @PathVariable(name = "permissionNames") List<String> permissionNames) {
        service.removePermissionsFromRole(roleId, permissionNames);
    }

    @GetMapping("/user-permissions")
    public List<String> getUserPermissions() {
        return service.getUserPermissions();
    }
}