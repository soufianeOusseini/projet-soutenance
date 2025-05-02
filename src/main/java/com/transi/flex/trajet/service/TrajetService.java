package com.transi.flex.trajet.service;

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

@Service
@AllArgsConstructor
public class TrajetService {

    private final TrajetMapper mapper;
    private final TrajetRepository repository;


    public List<TrajetDTO> getAll(){
        return mapper.toDtos(repository.findAll());
    }

    public TrajetDTO save(TrajetDTO dto){
        Trajet trajet = repository.save(mapper.toModel(dto));
        return mapper.toDto(trajet);
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
