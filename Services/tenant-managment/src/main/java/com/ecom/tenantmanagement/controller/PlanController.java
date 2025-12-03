package com.ecom.tenantmanagement.controller;

import com.ecom.tenantmanagement.dto.PlanCreateRequest;
import com.ecom.tenantmanagement.dto.PlanDTO;
import com.ecom.tenantmanagement.dto.PlanUpdateRequest;
import com.ecom.tenantmanagement.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(@RequestBody PlanCreateRequest request) {
        return ResponseEntity.ok(planService.createPlan(request));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable UUID planId, @RequestBody PlanUpdateRequest request) {
        return ResponseEntity.ok(planService.updatePlan(planId, request));
    }

    @GetMapping
    public ResponseEntity<Page<PlanDTO>> getAllPlans(@PageableDefault(size = 20, sort = "planName") Pageable pageable) {
        return ResponseEntity.ok(planService.getAllPlans(pageable));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable UUID planId) {
        return ResponseEntity.ok(planService.getPlanById(planId));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID planId) {
        planService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }
}

