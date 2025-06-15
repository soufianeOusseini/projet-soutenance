package com.transi.flex.colis.service;

import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.colis.mapper.ColisMapper;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.colis.repository.ColisRepository;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.CompanyContextHolder;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ColisService {

    private final ColisRepository repository;
    private final ColisMapper mapper;
    private final CompanyRepository companyRepository;

    public List<ColisDTO> getAll(){
        return mapper.toDtos(repository.findAll());
    }

    public ColisDTO save(ColisDTO dto){
        Colis colis = mapper.toModel(dto);
        Company company = companyRepository.findById(CompanyContextHolder.getCurrentId()).orElseThrow(() -> new EntityNotFoundException("Company not found"));
        colis.setCompany(company);
        colis.setStatus(ColisStatus.EN_ATTENTE);
        if (CollectionUtils.isNotEmpty(colis.getColisItems())) {
            colis.getColisItems().forEach(items -> {
                items.setColis(colis);
            });
        }

        return mapper.toDto(repository.save(colis));
    }

    public ColisDTO getColisById(Long id){
        Colis colis = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Colis not found"));
        return mapper.toDto(colis);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Colis not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
