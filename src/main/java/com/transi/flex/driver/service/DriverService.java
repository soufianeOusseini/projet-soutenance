package com.transi.flex.driver.service;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.enums.UserProfile;
import com.transi.flex.account.mapper.UserMapper;
import com.transi.flex.account.model.User;
import com.transi.flex.account.service.UserService;
import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.driver.dao.DriverDAO;
import com.transi.flex.driver.mapper.DriverMapper;
import com.transi.flex.driver.dto.DriverDTO;
import com.transi.flex.driver.model.Driver;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverMapper mapper;
    private final DriverDAO repository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AgencyRepository agencyRepository;


    public List<DriverDTO> getAll() {
        Long agencyId = AgencyContextHolder.getCurrentAgencyId();

        if (agencyId != null) {
            // Utilisateur d'agence
            return mapper.toDtos(repository.findByAgencyId(agencyId));
        } else {
            // Admin compagnie
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId)
                    .stream()
                    .map(Agency::getId)
                    .collect(Collectors.toList());

            List<Driver> drivers = agencyIds.stream()
                    .flatMap(id -> repository.findByAgencyId(id).stream())
                    .collect(Collectors.toList());

            return mapper.toDtos(drivers);
        }
    }


    public DriverDTO save(DriverDTO dto){
        Agency agency = agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId())
                .orElseThrow(() -> new EntityNotFoundException("Agency not found"));
        dto.getUser().addProfile(UserProfile.DRIVER);
        UserDTO user =  userService.add(dto.getUser());
        Driver driver = mapper.toModel(dto);
        driver.setAgency(agency);
        driver.setUser(userMapper.toModel(user));
        Driver saved = repository.save(driver);
        return mapper.toDto(saved);
    }

    public DriverDTO getDriver(Long id){
        Driver driver = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        return mapper.toDto(driver);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Driver not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
