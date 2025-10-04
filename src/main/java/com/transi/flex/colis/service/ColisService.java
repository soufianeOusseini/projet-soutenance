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
        return mapper.toDtos(repository.findByCompanyId(CompanyContextHolder.getCurrentId()));
    }

    public ColisDTO save(ColisDTO dto){
        Colis colis = mapper.toModel(dto);
        Company company = companyRepository.findById(CompanyContextHolder.getCurrentId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
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
        Colis colis = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colis not found"));
        return mapper.toDto(colis);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Colis not found with id: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Nouvelle méthode pour mettre à jour le statut d'un colis
     */
    @Transactional
    public ColisDTO updateStatus(Long id, ColisStatus newStatus) {
        Colis colis = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colis not found with id: " + id));

        // Vérifier que le colis appartient à la bonne compagnie
        if (!colis.getCompany().getId().equals(CompanyContextHolder.getCurrentId())) {
            throw new EntityNotFoundException("Colis not found");
        }

        // Valider la transition de statut
        validateStatusTransition(colis.getStatus(), newStatus);

        colis.setStatus(newStatus);
        return mapper.toDto(repository.save(colis));
    }

    /**
     * Méthode pour valider les transitions de statut
     */
    private void validateStatusTransition(ColisStatus currentStatus, ColisStatus newStatus) {
        if (currentStatus == null) {
            return; // Première assignation de statut
        }

        boolean isValidTransition = false;

        switch (currentStatus) {
            case EN_ATTENTE:
                isValidTransition = newStatus == ColisStatus.EN_TRANSIT || newStatus == ColisStatus.ANNULE;
                break;
            case EN_TRANSIT:
                isValidTransition = newStatus == ColisStatus.LIVRE || newStatus == ColisStatus.ANNULE;
                break;
            case LIVRE:
                // Un colis livré ne peut généralement pas changer de statut
                isValidTransition = false;
                break;
            case ANNULE:
                // Un colis annulé peut être remis en attente
                isValidTransition = newStatus == ColisStatus.EN_ATTENTE;
                break;
        }

        if (!isValidTransition) {
            throw new IllegalArgumentException(
                    String.format("Transition de statut invalide: de %s vers %s",
                            currentStatus, newStatus)
            );
        }
    }

    /**
     * Méthode pour obtenir les statuts de transition possibles
     */
    public List<ColisStatus> getAvailableStatusTransitions(Long id) {
        Colis colis = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colis not found"));

        ColisStatus currentStatus = colis.getStatus();

        return switch (currentStatus) {
            case EN_ATTENTE -> List.of(ColisStatus.EN_TRANSIT, ColisStatus.ANNULE);
            case EN_TRANSIT -> List.of(ColisStatus.LIVRE, ColisStatus.ANNULE);
            case LIVRE -> List.of(); // Aucune transition possible
            case ANNULE -> List.of(ColisStatus.EN_ATTENTE);
            default -> List.of();
        };
    }
}