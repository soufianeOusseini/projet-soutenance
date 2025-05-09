package com.transi.flex.company.service;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.service.UserService;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.mapper.CompanyMapper;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repository;
    private final CompanyMapper mapper;
    private final UserService userService;

    public CompanyDTO getById(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id)));
    }

    public List<CompanyDTO> getAll() {
        return mapper.toDtos(repository.findAll());
    }


    @Transactional
    public CompanyDTO add(CompanyDTO dto) {
        Company model = mapper.toModel(dto);
        if(dto.getId() == null) {
            repository.save(model);
            dto.setId(model.getId());
            createCompanyAdmin(dto);
        }
        return mapper.toDto(repository.save(model));
    }

    @Transactional
    public CompanyDTO update(CompanyDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Company ID cannot be null for update operation");
        }

        repository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + dto.getId()));

        Company model = mapper.toModel(dto);
        return mapper.toDto(repository.save(model));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Company not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private void createCompanyAdmin(CompanyDTO dto) {
            UserDTO user = new UserDTO();
            user.setEmail(dto.getAdminEmail());
            user.setUsername(dto.getAdminEmail());
            user.setFirstName(dto.getAdminFirstName());
            user.setLastName(dto.getAdminLastName());
            user.setPhone(dto.getAdminPhone());
            userService.addAdminUser(user, dto);

    }
}