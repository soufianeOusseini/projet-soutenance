package com.transi.flex.colis.service;

import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.service.UserService;
import com.transi.flex.agency.model.Agency;
import com.transi.flex.colis.dto.ColisDTO;
import com.transi.flex.colis.enums.ColisStatus;
import com.transi.flex.colis.mapper.ColisMapper;
import com.transi.flex.colis.model.Colis;
import com.transi.flex.colis.repository.ColisRepository;
import com.transi.flex.company.repository.CompanyRepository;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.pdf.PdfTicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import com.transi.flex.agency.dao.AgencyRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ColisService {

    private final ColisRepository repository;
    private final ColisMapper mapper;
    private final PdfTicketService pdfTicketService;
    private final AgencyRepository agencyRepository;
    private final UserService userService;

    public List<ColisDTO> getAll() {
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

            List<Colis> colis = agencyIds.stream()
                    .flatMap(id -> repository.findByAgencyId(id).stream())
                    .collect(Collectors.toList());

            return mapper.toDtos(colis);
        }
    }

    public ColisDTO save(ColisDTO dto){
        if(dto.getUser() !=null){
            UserDTO user = userService.getUserById(dto.getUser().getId());
            dto.setExpediteur(user.getFirstName());
            dto.setUser(user);
        }
        if(dto.getNumero() == null){
            dto.setNumero(generateUniqueNumber());
        }
        UserDTO createdBY = userService.getCurrentUser();
        dto.setCreatedBy(createdBY);
        Colis colis = mapper.toModel(dto);
        if(AgencyContextHolder.getCurrentAgencyId() !=null){
            Agency agency = agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Agency not found"));
            colis.setAgency(agency);
        }
//        if(dto.getAgency() !=null){
//            Agency agency = agencyRepository.findById(dto.getAgency().getId())
//                    .orElseThrow(() -> new EntityNotFoundException("Agency not found"));
//            colis.setAgency(agency);
//        }

        colis.setStatus(ColisStatus.EN_ATTENTE);
        if (CollectionUtils.isNotEmpty(colis.getColisItems())) {
            colis.getColisItems().forEach(items -> {
                items.setColis(colis);
            });
        }

        return mapper.toDto(repository.save(colis));
    }
    public String generateUniqueNumber() {
        Date date = new Date();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

        String year = yearFormat.format(date);
        String month = monthFormat.format(date);
        String day = dayFormat.format(date);

        int randomNumber = (int) (Math.random() * 10000);
        String randomPart = String.format("%04d", randomNumber);

        return "COL-" + year + month + day + "-" + randomPart;
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
        if (!colis.getAgency().getId().equals(AgencyContextHolder.getCurrentAgencyId())) {
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

    public List<ColisDTO> getByUser(Long id){
        return mapper.toDtos(repository.findByUserId(id));
    }

    public List<ColisDTO> getUserColis(){
        return mapper.toDtos(repository.findByUserId(userService.getCurrentUser().getId()));
    }

    public byte[] generateColisPdf(Long colisId) {
        Colis colis = repository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis non trouvé avec l'ID: " + colisId));

        return pdfTicketService.generateColisPdf(colis);
    }
}