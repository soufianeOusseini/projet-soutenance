package com.transi.flex.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transi.flex.account.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByIdIn(List<Long> ids);

    Permission findFirstByName(String permissionName);

    List<Permission> findByNameIn(List<String> name);

}