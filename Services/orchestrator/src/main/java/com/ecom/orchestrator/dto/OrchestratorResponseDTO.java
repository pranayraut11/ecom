package com.ecom.orchestrator.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrchestratorResponseDTO {

    private UUID orderId;
    private OrderStatus status;

}