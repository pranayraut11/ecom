package com.ecom.tenantmanagement.repository;

import com.ecom.tenantmanagement.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, UUID> {
    boolean existsByPlanNameIgnoreCase(String planName);
}

