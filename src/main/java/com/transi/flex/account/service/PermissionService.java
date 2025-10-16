package com.transi.flex.account.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.transi.flex.company.mapper.CompanyMapper;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.service.CompanyService;
import com.transi.flex.config.CompanyContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transi.flex.account.dto.PermissionDTO;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.mapper.PermissionMapper;
import com.transi.flex.account.model.Permission;
import com.transi.flex.account.model.Role;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.PermissionRepository;
import com.transi.flex.account.repository.RoleRepository;
import com.transi.flex.account.repository.UserRepository;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private  CompanyMapper companyMapper;

    @Transactional
    public void addPermissionToRole(Long roleId, String permissionName) {
        Role role = roleRepository.findById(roleId).orElse(null);
        Company company = companyMapper.toModel(companyService.getById(CompanyContextHolder.getCurrentId()));
        Permission permission = permissionRepository.findFirstByName(permissionName);
        permission.setCompany(company);
        if (role != null && permission != null) {
            if (!role.getPermissions().contains(permission)) {
                role.getPermissions().add(permission);
            }
        }
    }

    @Transactional
    public void removePermissionFromRole(Long roleId, String permissionName) {
        Role role = roleRepository.findById(roleId).orElse(null);
        Permission permission = permissionRepository.findFirstByName(permissionName);

        if (role != null && permission != null) {
            role.getPermissions().remove(permission);
        }
    }

    @Transactional
    public void addPermissionsToRole(Long roleId, List<String> permissionNames) {
        Role role = roleRepository.findById(roleId).orElse(null);
        List<Permission> permissions = permissionRepository.findByNameIn(permissionNames);

        if (role != null && !permissions.isEmpty()) {
            permissions.forEach(p -> {
                if (!role.getPermissions().contains(p)) {
                    role.getPermissions().add(p);
                }
            });
            roleRepository.save(role);
        }
    }

    @Transactional
    public void removePermissionsFromRole(Long roleId, List<String> permissionNames) {
        Role role = roleRepository.findById(roleId).orElse(null);
        List<Permission> permissions = permissionRepository.findByNameIn(permissionNames);

        if (role != null && !permissions.isEmpty()) {
            permissions.forEach(p -> {
                role.getPermissions().remove(p);
            });
            roleRepository.save(role);
        }
    }

    public List<Permission> getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null) {
            return role.getPermissions().stream().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<PermissionDTO> findAll() {
        return permissionMapper.toDtos(permissionRepository.findAll(Sort.by(Direction.DESC, "id")));
    }

    @Transactional
    public List<PermissionDTO> getByCompany() {
        return permissionMapper.toDtos(permissionRepository.findByCompanyId(Sort.by(Direction.DESC, "id"), CompanyContextHolder.getCurrentId()));
    }


    public List<String> getUserPermissions() {
        UserDTO userDTO = userService.getCurrentUser();
        if (userDTO == null) {
            return Collections.emptyList();
        }
        Optional<User> user = userRepository.findById(userDTO.getId());
        if (user.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> permissions = new ArrayList<>();
        if (user.get().isSuperAdmin()) {
            return getAllCompanyPermissions(user.get().getCompany().getId());
        }
        user.get().getRoles().forEach(role -> {
            permissions.add(role.getName());
            role.getPermissions().forEach(permission -> {
                permissions.add(permission.getName());
            });
        });
        return permissions;
    }

    private List<String> getAllCompanyPermissions(Long companyId) {
        List<Role> companyRoles = roleRepository.findByCompanyId(companyId);
        List<String> permissions = new ArrayList<>();
        companyRoles.forEach(role -> {
            permissions.add(role.getName());
            role.getPermissions().forEach(permission -> {
                permissions.add(permission.getName());
            });
        });
        return permissions;
    }
}