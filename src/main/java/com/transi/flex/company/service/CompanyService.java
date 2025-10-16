package com.transi.flex.company.service;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.dto.UserSummary;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.account.service.UserService;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.enums.CompanyStatus;
import com.transi.flex.company.mapper.CompanyMapper;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.file.enums.FileType;
import com.transi.flex.file.service.FileUtility;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repository;
    private final CompanyMapper mapper;
    private final UserService userService;
    private final FileUtility fileUtility;

    private final UserRepository userRepository;
    public CompanyDTO getById(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id)));
    }

    public List<CompanyDTO> getAll() {
        List<Company> companies = repository.findAll();
        return companies.stream().map(mapper::toDto).collect(Collectors.toList());
    }


    @Transactional
    public CompanyDTO add(CompanyDTO dto) {
        Company model = mapper.toModel(dto);

        if (dto.getId() == null) {
            model.setStatus(CompanyStatus.ACTIVE);
            repository.save(model);
            dto.setId(model.getId());
            createCompanyAdmin(dto);
        } else {
            repository.save(model);
        }
        return mapper.toDto(model);
    }


    @Transactional
    public CompanyDTO update(CompanyDTO dto, Optional<MultipartFile> logoPath) throws Exception {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Company ID cannot be null for update operation");
        }

        repository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + dto.getId()));
        Company model = mapper.toModel(dto);
        saveLogo(logoPath, model);
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

    public void changeStatus(Long id){
        Company company = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id) );
        switch (company.getStatus()){
            case ACTIVE -> company.setStatus(CompanyStatus.INACTIVE);
            case INACTIVE -> company.setStatus(CompanyStatus.ACTIVE);
        }
        repository.save(company);
    }

    private void saveLogo(Optional<MultipartFile> logoPath,
                                   Company company) throws Exception {
        if (company.getLogoPath() !=null){
            fileUtility.deleteFile(company.getLogoPath());
        }
        if (logoPath.isPresent()) {
            String logoFilePath = fileUtility.save(logoPath.get(), logoPath.get().getOriginalFilename(),
                    FileType.COMPANY_LOGO);
            company.setLogoPath(logoFilePath);
        }

    }
}