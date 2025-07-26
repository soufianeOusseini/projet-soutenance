package com.transi.flex.bus.service;

import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.enums.BusStatus;
import com.transi.flex.bus.mapper.BusMapper;
import com.transi.flex.bus.model.Bus;
import com.transi.flex.bus.repository.BusRepository;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.file.enums.FileType;
import com.transi.flex.file.service.FileUtility;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    private final BusMapper mapper;

    private final CompanyRepository companyRepository;

    private final FileUtility fileUtility;

    public BusDTO save(BusDTO dto, Optional<MultipartFile> image) throws Exception {
        Bus model = mapper.toModel(dto);
        Company company = companyRepository.findById(CompanyContextHolder.getCurrentId()).orElseThrow(() -> new EntityNotFoundException("Company not found"));
        model.setCompany(company);
        model.setStatus(BusStatus.ACTIVE);
        model.setSpaceAvailable(dto.getCapacity());
        saveImage(image, model);
        return mapper.toDto(busRepository.save(model));
    }

    public BusDTO getBusById(Long id){
        Bus bus = busRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bus not found"));
        return mapper.toDto(bus);
    }

    public List<BusDTO> getAll(){
        List<Bus> buses = busRepository.findByCompanyId(CompanyContextHolder.getCurrentId());
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
