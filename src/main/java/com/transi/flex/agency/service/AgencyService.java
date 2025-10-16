package com.transi.flex.agency.service;

import com.transi.flex.agency.dto.AgencyDTO;
import com.transi.flex.agency.mapper.AgencyMapper;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.agency.enums.AgencyStatus;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.CompanyContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final CompanyRepository companyRepository;
    private final AgencyMapper agencyMapper;

    public List<AgencyDTO> getAgenciesByCompanyId(Long companyId) {
        return agencyMapper.toDtos(agencyRepository.findByCompanyId(companyId));
    }

    public AgencyDTO getAgencyById(Long id) {
        return agencyMapper.toDto(agencyRepository.findById(id).get());
    }

    public AgencyDTO createAgency(AgencyDTO agencyDTO) {

        if (agencyRepository.existsByCodeAndCompanyId(agencyDTO.getCode(), agencyDTO.getCompanyId())) {
            throw new IllegalArgumentException("Le code d'agence existe déjà pour cette compagnie");
        }

        Company company = companyRepository.findById(CompanyContextHolder.getCurrentId()).orElse(null);

        Agency agency = agencyMapper.toModel(agencyDTO);
        agency.setCompany(company);

        Agency savedAgency = agencyRepository.save(agency);
        return agencyMapper.toDto(savedAgency);
    }

    public AgencyDTO updateAgency(Long id, AgencyDTO agencyDTO) {

        Agency existingAgency = agencyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agence non trouvée"));

        // Vérifier si le nouveau code existe déjà (sauf pour l'agence actuelle)
        if (!existingAgency.getCode().equals(agencyDTO.getCode()) &&
                agencyRepository.existsByCodeAndCompanyId(agencyDTO.getCode(), agencyDTO.getCompanyId())) {
            throw new IllegalArgumentException("Le code d'agence existe déjà pour cette compagnie");
        }

        updateAgencyFromDTO(existingAgency, agencyDTO);
        Agency updatedAgency = agencyRepository.save(existingAgency);
        return agencyMapper.toDto(updatedAgency);
    }

    public void deleteAgency(Long id) {
        if (!agencyRepository.existsById(id)) {
            throw new IllegalArgumentException("Agence non trouvée");
        }
        agencyRepository.deleteById(id);
    }

    public AgencyDTO changeAgencyStatus(Long id, AgencyStatus status) {

        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agence non trouvée"));

        agency.setStatus(status);
        Agency updatedAgency = agencyRepository.save(agency);
        return agencyMapper.toDto(updatedAgency);
    }

    public List<AgencyDTO> searchAgencies(Long companyId, String keyword) {
        return agencyMapper.toDtos(agencyRepository.searchByCompanyIdAndKeyword(companyId, keyword));

    }

    public long countActiveAgenciesByCompany(Long companyId) {
        return agencyRepository.countByCompanyIdAndStatus(companyId, AgencyStatus.ACTIVE);
    }
    private void updateAgencyFromDTO(Agency agency, AgencyDTO dto) {
        agency.setName(dto.getName());
        agency.setCode(dto.getCode());
        agency.setAddress(dto.getAddress());
        agency.setTelephone(dto.getTelephone());
        agency.setCity(dto.getCity());
        agency.setRegion(dto.getRegion());
        agency.setEmail(dto.getEmail());
        agency.setManagerName(dto.getManagerName());
        agency.setManagerPhone(dto.getManagerPhone());
        agency.setStatus(dto.getStatus());
    }


    public List<AgencyDTO> findAll(){
        return agencyMapper.toDtos(agencyRepository.findAll());
    }


    public AgencyDTO getById(Long currentAgencyId) {
        return agencyMapper.toDto(agencyRepository.findById(currentAgencyId).orElse(null));
    }
}