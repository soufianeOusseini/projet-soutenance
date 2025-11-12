package com.transi.flex.trajet.service;

import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.trajet.dto.TrajetDTO;
import com.transi.flex.trajet.mapper.TrajetMapper;
import com.transi.flex.trajet.model.Trajet;
import com.transi.flex.trajet.repository.TrajetRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrajetService {

    private final TrajetMapper mapper;
    private final TrajetRepository repository;
    private final AgencyRepository agencyRepository;


    public List<TrajetDTO> getAll() {
        Long agencyId = AgencyContextHolder.getCurrentAgencyId();

        if (agencyId != null) {
            return mapper.toDtos(repository.findByAgencyId(agencyId));
        } else {
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId)
                    .stream()
                    .map(Agency::getId)
                    .collect(Collectors.toList());

            return agencyIds.stream()
                    .flatMap(id -> repository.findByAgencyId(id).stream())
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    public TrajetDTO save(TrajetDTO dto){
        Agency agency = agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId())
                .orElseThrow(() -> new EntityNotFoundException("Agency not found"));
        Trajet trajet = mapper.toModel(dto);
        trajet.setAgency(agency);
        Trajet saved = repository.save(trajet);
        return mapper.toDto(saved);
    }

    public TrajetDTO getTrajetById(Long id){
        Trajet trajet = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Trajet not found"));
        return mapper.toDto(trajet);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Trajet not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
