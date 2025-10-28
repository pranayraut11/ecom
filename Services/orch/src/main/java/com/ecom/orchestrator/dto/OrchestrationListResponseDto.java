package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationListResponseDto {

    private Long id;
    private String orchName;
    private String type;
    private String status;
    private String initiatorName;
    private Integer registeredWorkersCount;
    private Integer totalWorkersExpected;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUpdated;
}
