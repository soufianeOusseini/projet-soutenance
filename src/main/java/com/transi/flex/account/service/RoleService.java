package com.transi.flex.account.service;

import java.util.ArrayList;
import java.util.List;

import com.transi.flex.agency.mapper.AgencyMapper;
import com.transi.flex.agency.service.AgencyService;
import com.transi.flex.company.mapper.CompanyMapper;
import com.transi.flex.company.service.CompanyService;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
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
    private final AgencyService agencyService;
    private final AgencyMapper agencyMapper;

    @Transactional
    public RoleDTO add(RoleDTO dto) {
        Role model = mapper.toModel(dto);
        model.setAgency(agencyMapper.toModel(agencyService.getById(AgencyContextHolder.getCurrentAgencyId())));
        return mapper.toDto(repository.save(model));
    }

    public RoleDTO getById(Long id) {
        return mapper.toDto(repository.findById(id).orElse(null));
    }

    public List<RoleDTO> getAll() {
        return mapper.toDtos(repository.findAll());
    }

    public List<RoleDTO> getByAgency() {
        Long currentAgencyId = AgencyContextHolder.getCurrentAgencyId();
        return mapper.toDtos(repository.findAllByCompanyOrSystem(currentAgencyId));
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