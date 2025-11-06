package com.ecom.orchestrator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Retry policy configuration for orchestration execution")
public class RetryPolicyDto {

    @Schema(description = "Maximum number of retries allowed", example = "3")
    private Integer maxRetries;

    @Schema(description = "Backoff time between retries in milliseconds", example = "5000")
    private Long backoffMs;
}

