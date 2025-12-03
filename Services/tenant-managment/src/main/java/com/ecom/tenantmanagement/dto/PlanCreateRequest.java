package com.ecom.tenantmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCreateRequest {
    private String planName;
    private BigDecimal price;
    private String billingCycle;
    private Integer maxProducts;
    private Integer maxOrders;
    private Integer maxStorage;
    private String defaultFeatures;
}

