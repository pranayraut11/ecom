package com.ecom.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedOrchestrationResponseDto {

    private List<OrchestrationListResponseDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
