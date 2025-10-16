package com.transi.flex.bus.service;

import com.transi.flex.agency.model.Agency;
import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.enums.BusStatus;
import com.transi.flex.bus.mapper.BusMapper;
import com.transi.flex.bus.model.Bus;
import com.transi.flex.bus.repository.BusRepository;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.AgencyContextHolder;
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
        saveImage(image, model);

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

    public List<BusDTO> getAll(){
        List<Bus> buses = busRepository.findByAgencyId(AgencyContextHolder.getCurrentAgencyId());
        return mapper.toDtos(buses);
    }


    @Transactional
    public void delete(Long id) {
        if (!busRepository.existsById(id)) {
            throw new EntityNotFoundException("Colis not found with id: " + id);
        }
        busRepository.deleteById(id);
    }

    private void saveImage(Optional<MultipartFile> image,
                          Bus bus) throws Exception {
        if (bus.getImage() !=null){
            fileUtility.deleteFile(bus.getImage());
        }
        if (image.isPresent()) {
            String logoFilePath = fileUtility.save(image.get(), image.get().getOriginalFilename(),
                    FileType.BUS_IMAGE);
            bus.setImage(logoFilePath);
        }

    }
}
