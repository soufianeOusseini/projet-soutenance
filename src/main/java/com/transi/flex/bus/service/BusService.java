package com.transi.flex.bus.service;

import com.transi.flex.bus.dto.BusDTO;
import com.transi.flex.bus.mapper.BusMapper;
import com.transi.flex.bus.model.Bus;
import com.transi.flex.bus.repository.BusRepository;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.CompanyContextHolder;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    private final BusMapper mapper;

    private final CompanyRepository companyRepository;

    public BusDTO save(BusDTO dto){
        Bus model = busRepository.save(mapper.toModel(dto));
        Company company = companyRepository.findById(CompanyContextHolder.getCurrentId()).orElseThrow(() -> new EntityNotFoundException("Company not found"));
        model.setCompany(company);
        return mapper.toDto(model);
    }

    public BusDTO getBusById(Long id){
        Bus bus = busRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bus not found"));
        return mapper.toDto(bus);
    }

    public List<BusDTO> getAll(){
        List<Bus> buses = busRepository.findAll();
        return mapper.toDtos(buses);
    }


    @Transactional
    public void delete(Long id) {
        if (!busRepository.existsById(id)) {
            throw new EntityNotFoundException("Colis not found with id: " + id);
        }
        busRepository.deleteById(id);
    }
}
