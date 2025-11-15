package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.ExecutionDetailsResponseDto;
import com.ecom.orchestrator.service.ExecutionDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Execution Details", description = "APIs for retrieving detailed step-level execution information")
public class ExecutionDetailsController {

    private final ExecutionDetailsService executionDetailsService;

    @GetMapping("/orchestrations/{orchName}/executions/{executionId}")
    @Operation(
        summary = "Get detailed step-level execution information for a specific orchestration run",
        description = "Retrieve comprehensive details about all step executions within a specific orchestration run, including timing and status information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Execution details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Orchestration or execution not found")
    })
    public ResponseEntity<ExecutionDetailsResponseDto> getExecutionDetails(
            @Parameter(description = "Orchestration name", example = "tenantCreation", required = true)
            @PathVariable String orchName,
            
            @Parameter(description = "Execution identifier", example = "f14a9c8b-1234", required = true)
            @PathVariable String executionId) {

        try {
            log.info("Received request to get execution details for orchestration: {} and executionId: {}", 
                    orchName, executionId);

            ExecutionDetailsResponseDto details = executionDetailsService.getExecutionDetails(orchName, executionId);

            if (details == null) {
                log.warn("Orchestration or execution not found - orchName: {}, executionId: {}", orchName, executionId);
                return ResponseEntity.notFound().build();
            }

            log.info("Successfully retrieved execution details for: {} with {} steps", 
                    executionId, details.getSteps().size());

            return ResponseEntity.ok(details);

        } catch (Exception e) {
            log.error("Error retrieving execution details for orchestration: {} and executionId: {}", 
                    orchName, executionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
