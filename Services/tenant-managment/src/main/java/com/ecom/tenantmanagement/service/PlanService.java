package com.ecom.tenantmanagement.service;

import com.ecom.tenantmanagement.dto.PlanCreateRequest;
import com.ecom.tenantmanagement.dto.PlanDTO;
import com.ecom.tenantmanagement.dto.PlanUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PlanService {
    PlanDTO createPlan(PlanCreateRequest request);
    PlanDTO updatePlan(UUID planId, PlanUpdateRequest request);
    Page<PlanDTO> getAllPlans(Pageable pageable);
    PlanDTO getPlanById(UUID planId);
    void deletePlan(UUID planId);
}

