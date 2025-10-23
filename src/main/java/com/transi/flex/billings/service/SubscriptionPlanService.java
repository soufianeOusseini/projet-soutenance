package com.transi.flex.billings.service;

import com.transi.flex.billings.dao.SubscriptionPlanDAO;
import org.springframework.stereotype.Service;


import com.transi.flex.billings.dto.CreateSubscriptionPlanDTO;
import com.transi.flex.billings.dto.SubscriptionPlanDTO;
import com.transi.flex.billings.mapper.SubscriptionPlanMapper;
import com.transi.flex.billings.model.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionPlanService {

    private final SubscriptionPlanDAO planRepository;
    private final SubscriptionPlanMapper mapper;

    public SubscriptionPlanDTO createPlan(CreateSubscriptionPlanDTO dto) {
        SubscriptionPlan plan = mapper.toModel(dto);
        SubscriptionPlan saved = planRepository.save(plan);
        return mapper.toDto(saved);
    }

    public SubscriptionPlanDTO updatePlan(Long id, CreateSubscriptionPlanDTO dto) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setDurationInDays(dto.getDurationInDays());
        plan.setDescription(dto.getDescription());

        SubscriptionPlan saved = planRepository.save(plan);
        return mapper.toDto(saved);
    }

    public void deactivatePlan(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        plan.setActive(false);
        planRepository.save(plan);
    }

    public void activatePlan(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        plan.setActive(true);
        planRepository.save(plan);
    }

    public SubscriptionPlanDTO getPlanById(Long id) {
        return planRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }

    public List<SubscriptionPlanDTO> getAllPlans() {
        return planRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SubscriptionPlanDTO> getActivePlans() {
        return planRepository.findByActiveTrue()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}