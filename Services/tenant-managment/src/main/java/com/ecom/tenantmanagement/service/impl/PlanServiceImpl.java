package com.ecom.tenantmanagement.service.impl;

import com.ecom.tenantmanagement.dto.PlanCreateRequest;
import com.ecom.tenantmanagement.dto.PlanDTO;
import com.ecom.tenantmanagement.dto.PlanUpdateRequest;
import com.ecom.tenantmanagement.entity.PlanEntity;
import com.ecom.tenantmanagement.repository.PlanRepository;
import com.ecom.tenantmanagement.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;

    private PlanDTO toDTO(PlanEntity entity) {
        return new PlanDTO(
            entity.getPlanId(),
            entity.getPlanName(),
            entity.getPrice(),
            entity.getBillingCycle(),
            entity.getMaxProducts(),
            entity.getMaxOrders(),
            entity.getMaxStorage(),
            entity.getDefaultFeatures()
        );
    }

    @Override
    public PlanDTO createPlan(PlanCreateRequest request) {
        PlanEntity entity = new PlanEntity(
            null,
            request.getPlanName(),
            request.getPrice(),
            request.getBillingCycle(),
            request.getMaxProducts(),
            request.getMaxOrders(),
            request.getMaxStorage(),
            request.getDefaultFeatures()
        );
        return toDTO(planRepository.save(entity));
    }

    @Override
    public PlanDTO updatePlan(UUID planId, PlanUpdateRequest request) {
        PlanEntity entity = planRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        entity.setPlanName(request.getPlanName());
        entity.setPrice(request.getPrice());
        entity.setBillingCycle(request.getBillingCycle());
        entity.setMaxProducts(request.getMaxProducts());
        entity.setMaxOrders(request.getMaxOrders());
        entity.setMaxStorage(request.getMaxStorage());
        return toDTO(planRepository.save(entity));
    }

    @Override
    public Page<PlanDTO> getAllPlans(Pageable pageable) {
        return planRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public PlanDTO getPlanById(UUID planId) {
        return planRepository.findById(planId).map(this::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
    }

    @Override
    public void deletePlan(UUID planId) {
        planRepository.deleteById(planId);
    }
}

