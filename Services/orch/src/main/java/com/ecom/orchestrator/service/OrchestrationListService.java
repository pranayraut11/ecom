package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.OrchestrationListRequestDto;
import com.ecom.orchestrator.dto.OrchestrationListResponseDto;
import com.ecom.orchestrator.dto.PagedOrchestrationResponseDto;
import com.ecom.orchestrator.entity.OrchestrationStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.entity.OrchestrationTypeEnum;
import com.ecom.orchestrator.repository.OrchestrationListRepository;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import com.ecom.orchestrator.specification.OrchestrationSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestrationListService {

    private final OrchestrationListRepository orchestrationListRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;

    public PagedOrchestrationResponseDto listOrchestrations(OrchestrationListRequestDto request) {

        log.info("Listing orchestrations with filters - page: {}, size: {}, sortBy: {}, direction: {}, status: {}, type: {}, orchName: {}",
                request.getPage(), request.getSize(), request.getSortBy(), request.getDirection(),
                request.getStatus(), request.getType(), request.getOrchName());

        // Create sort object
        Sort sort = createSort(request.getSortBy(), request.getDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Build dynamic specification
        Specification<OrchestrationTemplate> spec = buildSpecification(request.getStatus(), request.getType(),
                request.getOrchName(), request.getRegisteredFrom(), request.getRegisteredTo());

        // Execute query
        Page<OrchestrationTemplate> orchestrationPage = orchestrationListRepository.findAll(spec, pageable);

        // Convert to DTOs
        List<OrchestrationListResponseDto> content = orchestrationPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PagedOrchestrationResponseDto.builder()
                .content(content)
                .page(request.getPage())
                .size(request.getSize())
                .totalElements(orchestrationPage.getTotalElements())
                .totalPages(orchestrationPage.getTotalPages())
                .build();
    }

    private Sort createSort(String sortBy, String direction) {
        // Default sorting
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }

        // Validate sort direction
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        }

        // Map API field names to entity field names
        String entityFieldName = mapToEntityField(sortBy);

        return Sort.by(sortDirection, entityFieldName);
    }

    private String mapToEntityField(String apiFieldName) {
        return switch (apiFieldName.toLowerCase()) {
            case "orchname" -> "orchName";
            case "type" -> "type";
            case "status" -> "status";
            case "initiatorname" -> "initiatorService";
            case "lastupdated" -> "createdAt";
            default -> "createdAt";
        };
    }

    private Specification<OrchestrationTemplate> buildSpecification(
            String status,
            String type,
            String orchName,
            LocalDateTime registeredFrom,
            LocalDateTime registeredTo) {

        Specification<OrchestrationTemplate> spec = Specification.where(null);

        // Status filter
        if (status != null && !status.isEmpty()) {
            try {
                OrchestrationStatusEnum statusEnum = mapApiStatusToEnum(status);
                spec = spec.and(OrchestrationSpecifications.hasStatus(statusEnum));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
            }
        }

        // Type filter
        if (type != null && !type.isEmpty()) {
            try {
                OrchestrationTypeEnum typeEnum = OrchestrationTypeEnum.valueOf(type.toUpperCase());
                spec = spec.and(OrchestrationSpecifications.hasType(typeEnum));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid type filter: {}", type);
            }
        }

        // Orchestration name filter (case-insensitive contains)
        if (orchName != null && !orchName.trim().isEmpty()) {
            spec = spec.and(OrchestrationSpecifications.orchNameContains(orchName.trim()));
        }

        // Date range filters
        if (registeredFrom != null) {
            spec = spec.and(OrchestrationSpecifications.createdAfter(registeredFrom));
        }

        if (registeredTo != null) {
            spec = spec.and(OrchestrationSpecifications.createdBefore(registeredTo));
        }

        return spec;
    }

    private OrchestrationStatusEnum mapApiStatusToEnum(String apiStatus) {
        return switch (apiStatus.toUpperCase()) {
            case "REGISTERED" -> OrchestrationStatusEnum.SUCCESS;
            case "PARTIALLY_REGISTERED" -> OrchestrationStatusEnum.PENDING;
            case "FAILED" -> OrchestrationStatusEnum.FAILED;
            default -> throw new IllegalArgumentException("Invalid status: " + apiStatus);
        };
    }

    private String mapEnumToApiStatus(OrchestrationStatusEnum status) {
        return switch (status) {
            case SUCCESS -> "REGISTERED";
            case PENDING -> "PARTIALLY_REGISTERED";
            case FAILED -> "FAILED";
        };
    }

    private OrchestrationListResponseDto convertToDto(OrchestrationTemplate template) {
        // Calculate worker counts
        int totalWorkersExpected = template.getSteps() != null ? template.getSteps().size() : 0;
        int registeredWorkersCount = workerRegistrationRepository.countByOrchName(template.getOrchName());

        return OrchestrationListResponseDto.builder()
                .id(template.getId())
                .orchName(template.getOrchName())
                .type(template.getType().name())
                .status(mapEnumToApiStatus(template.getStatus()))
                .initiatorName(template.getInitiatorService())
                .registeredWorkersCount(registeredWorkersCount)
                .totalWorkersExpected(totalWorkersExpected)
                .lastUpdated(template.getCreatedAt())
                .build();

    }
}
