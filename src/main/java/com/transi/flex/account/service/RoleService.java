package com.transi.flex.account.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.transi.flex.account.dto.RoleDTO;
import com.transi.flex.account.mapper.RoleMapper;
import com.transi.flex.account.model.Role;
import com.transi.flex.account.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    @Transactional
    public RoleDTO add(RoleDTO dto) {
        Role model = mapper.toModel(dto);
        return mapper.toDto(repository.save(model));
    }

    public RoleDTO getById(Long id) {
        return mapper.toDto(repository.findById(id).orElse(null));
    }

    public List<RoleDTO> getAll() {
        return mapper.toDtos(repository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void update(RoleDTO dto) {
        repository.save(mapper.toModel(dto));
    }
}