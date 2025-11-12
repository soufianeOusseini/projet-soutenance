package com.transi.flex.bus.service;

import com.transi.flex.agency.model.Agency;
import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.enums.BusStatus;
import com.transi.flex.bus.mapper.BusMapper;
import com.transi.flex.bus.model.Bus;
import com.transi.flex.bus.repository.BusRepository;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.file.enums.FileType;
import com.transi.flex.file.service.FileUtility;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.transi.flex.agency.dao.AgencyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    private final BusMapper mapper;

    private final CompanyRepository companyRepository;

    private final FileUtility fileUtility;

    private final AgencyRepository agencyRepository;

    public BusDTO save(BusDTO dto, Optional<MultipartFile> image) throws Exception {
        Bus model = mapper.toModel(dto);
        Agency agency = agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId())
                .orElseThrow(() -> new EntityNotFoundException("Agency not found"));
        model.setAgency(agency);
        model.setStatus(BusStatus.ACTIVE);
        model.setSpaceAvailable(dto.getCapacity());
        saveImage(image, model,agency.getCompany());

        boolean exists = (dto.getId() == null)
                ? busRepository.existsByNumberAndAgencyId(dto.getNumber(), agency.getId())
                : busRepository.existsByNumberAndAgencyIdAndIdNot(dto.getNumber(), agency.getId(), dto.getId());

        if (exists) {
            throw new IllegalArgumentException("Un bus avec ce numéro existe déjà pour cette agence.");
        }

        return mapper.toDto(busRepository.save(model));
    }


    public BusDTO getBusById(Long id){
        Bus bus = busRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bus not found"));
        return mapper.toDto(bus);
    }

    public List<BusDTO> getAll() {
        Long agencyId = AgencyContextHolder.getCurrentAgencyId();

        if (agencyId != null) {
            // Utilisateur d'agence
            List<Bus> buses = busRepository.findByAgencyId(agencyId);
            return mapper.toDtos(buses);
        } else {
            // Admin compagnie
            Long companyId = CompanyContextHolder.getCurrentId();
            List<Long> agencyIds = agencyRepository.findByCompanyId(companyId)
                    .stream()
                    .map(Agency::getId)
                    .collect(Collectors.toList());

            List<Bus> buses = agencyIds.stream()
                    .flatMap(id -> busRepository.findByAgencyId(id).stream())
                    .collect(Collectors.toList());

            return mapper.toDtos(buses);
        }
    }


    @Transactional
    public void delete(Long id) {
        if (!busRepository.existsById(id)) {
            throw new EntityNotFoundException("Colis not found with id: " + id);
        }
        busRepository.deleteById(id);
    }

    private void saveImage(Optional<MultipartFile> image,
                           Bus bus, Company company) throws Exception {
        if (bus.getImage() !=null){
            fileUtility.deleteFile(bus.getImage(),company);
        }
        if (image.isPresent()) {
            String logoFilePath = fileUtility.save(image.get(), image.get().getOriginalFilename(),
                    FileType.BUS_IMAGE, company);
            bus.setImage(logoFilePath);
        }

    }
}
