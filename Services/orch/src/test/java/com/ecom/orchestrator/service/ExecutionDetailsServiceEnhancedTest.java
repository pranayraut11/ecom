package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionDetailsResponseDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.OrchestrationRunRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionDetailsServiceEnhancedTest {

    @Mock
    private OrchestrationRunRepository orchestrationRunRepository;

    @Mock
    private OrchestrationTemplateRepository orchestrationTemplateRepository;

    @InjectMocks
    private ExecutionDetailsService executionDetailsService;

    @Test
    void testGetExecutionDetails_WithAllEnhancements() {
        // Given
        String orchName = "tenantCreation";
        String executionId = "test-execution-123";

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(5);
        LocalDateTime endTime = LocalDateTime.now();

        // Mock template
        OrchestrationTemplate template = OrchestrationTemplate.builder()
                .orchName(orchName)
                .type(OrchestrationTypeEnum.SEQUENTIAL)
                .initiatorService("tenant-management-service")
                .build();

        // Mock orchestration run
        OrchestrationRun run = OrchestrationRun.builder()
                .flowId(executionId)
                .orchName(orchName)
                .status(ExecutionStatusEnum.FAILED)
                .triggeredBy("USER")
                .correlationId("tenant-xyz-2025-11-03")
                .startedAt(startTime)
                .completedAt(endTime)
                .lastUpdatedAt(endTime)
                .build();

        // Mock step runs
        List<OrchestrationStepRun> stepRuns = new ArrayList<>();

        OrchestrationStepRun step1 = OrchestrationStepRun.builder()
                .id(1L)
                .orchestrationRun(run)
                .stepName("createRealm")
                .seq(1)
                .status(ExecutionStatusEnum.DO_SUCCESS)
                .operationType("DO")
                .workerService("worker-realm-service")
                .retryCount(0)
                .maxRetries(3)
                .startedAt(startTime)
                .completedAt(startTime.plusSeconds(10))
                .rollbackTriggered(false)
                .build();

        OrchestrationStepRun step2 = OrchestrationStepRun.builder()
                .id(2L)
                .orchestrationRun(run)
                .stepName("createClient")
                .seq(2)
                .status(ExecutionStatusEnum.FAILED)
                .operationType("DO")
                .workerService("worker-client-service")
                .errorMessage("Client already exists")
                .failureReason("Client already exists")
                .retryCount(3)
                .maxRetries(3)
                .lastRetryAt(startTime.plusSeconds(20))
                .startedAt(startTime.plusSeconds(11))
                .completedAt(startTime.plusSeconds(15))
                .rollbackTriggered(false)
                .build();

        stepRuns.add(step1);
        stepRuns.add(step2);
        run.setStepRuns(stepRuns);

        when(orchestrationTemplateRepository.findByOrchName(orchName))
                .thenReturn(Optional.of(template));
        when(orchestrationRunRepository.findByFlowIdWithSteps(executionId))
                .thenReturn(Optional.of(run));

        // When
        ExecutionDetailsResponseDto result = executionDetailsService.getExecutionDetails(orchName, executionId);

        // Then
        assertNotNull(result);

        // Verify core fields
        assertEquals(executionId, result.getExecutionId());
        assertEquals(orchName, result.getOrchName());
        assertEquals("FAILED", result.getStatus());
        assertEquals("SEQUENTIAL", result.getType());

        // Verify initiator information
        assertEquals("tenant-management-service", result.getInitiator());
        assertEquals("USER", result.getTriggeredBy());
        assertEquals("tenant-xyz-2025-11-03", result.getCorrelationId());

        // Verify timing
        assertEquals(startTime, result.getStartedAt());
        assertEquals(endTime, result.getCompletedAt());
        assertNotNull(result.getOverallDurationMs());
        assertTrue(result.getOverallDurationMs() > 0);

        // Verify statistics
        assertEquals(2, result.getTotalSteps());
        assertEquals(1, result.getSuccessfulSteps());
        assertEquals(1, result.getFailedSteps());
        assertEquals(0, result.getRolledBackSteps());
        assertEquals(50.0, result.getPercentageCompleted(), 0.1);

        // Verify retry policy
        assertNotNull(result.getRetryPolicy());
        assertEquals(3, result.getRetryPolicy().getMaxRetries());
        assertEquals(5000L, result.getRetryPolicy().getBackoffMs());

        // Verify steps
        assertEquals(2, result.getSteps().size());

        // Verify first step
        var firstStep = result.getSteps().get(0);
        assertEquals("createRealm", firstStep.getName());
        assertEquals("DO", firstStep.getOperationType());
        assertEquals("SUCCESS", firstStep.getStatus());
        assertEquals("worker-realm-service", firstStep.getExecutedBy());
        assertEquals(0, firstStep.getRetryCount());
        assertFalse(firstStep.getRollbackTriggered());

        // Verify second step
        var secondStep = result.getSteps().get(1);
        assertEquals("createClient", secondStep.getName());
        assertEquals("DO", secondStep.getOperationType());
        assertEquals("FAILED", secondStep.getStatus());
        assertEquals("worker-client-service", secondStep.getExecutedBy());
        assertEquals("Client already exists", secondStep.getFailureReason());
        assertEquals(3, secondStep.getRetryCount());
        assertNotNull(secondStep.getLastRetryAt());

        // Verify timeline exists and has events
        assertNotNull(result.getTimeline());
        assertTrue(result.getTimeline().size() > 0);

        // Verify timeline contains expected events
        boolean hasOrchStarted = result.getTimeline().stream()
                .anyMatch(e -> "ORCHESTRATION_STARTED".equals(e.getEvent()));
        assertTrue(hasOrchStarted);

        boolean hasStepFailed = result.getTimeline().stream()
                .anyMatch(e -> "STEP_FAILED".equals(e.getEvent()));
        assertTrue(hasStepFailed);
    }

    @Test
    void testCalculatePercentageCompleted() {
        // Test percentage calculation
        String orchName = "test";
        String executionId = "test-123";

        OrchestrationTemplate template = OrchestrationTemplate.builder()
                .orchName(orchName)
                .type(OrchestrationTypeEnum.SEQUENTIAL)
                .initiatorService("test-service")
                .build();

        OrchestrationRun run = OrchestrationRun.builder()
                .flowId(executionId)
                .orchName(orchName)
                .status(ExecutionStatusEnum.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();

        List<OrchestrationStepRun> stepRuns = new ArrayList<>();

        // 3 successful, 1 failed, 1 pending = 60% complete
        for (int i = 0; i < 3; i++) {
            stepRuns.add(createStepRun(i + 1, ExecutionStatusEnum.DO_SUCCESS, run));
        }
        stepRuns.add(createStepRun(4, ExecutionStatusEnum.FAILED, run));
        stepRuns.add(createStepRun(5, ExecutionStatusEnum.PENDING, run));

        run.setStepRuns(stepRuns);

        when(orchestrationTemplateRepository.findByOrchName(orchName))
                .thenReturn(Optional.of(template));
        when(orchestrationRunRepository.findByFlowIdWithSteps(executionId))
                .thenReturn(Optional.of(run));

        ExecutionDetailsResponseDto result = executionDetailsService.getExecutionDetails(orchName, executionId);

        assertEquals(5, result.getTotalSteps());
        assertEquals(3, result.getSuccessfulSteps());
        assertEquals(1, result.getFailedSteps());
        assertEquals(60.0, result.getPercentageCompleted(), 0.1);
    }

    private OrchestrationStepRun createStepRun(int seq, ExecutionStatusEnum status, OrchestrationRun run) {
        return OrchestrationStepRun.builder()
                .seq(seq)
                .stepName("step" + seq)
                .status(status)
                .operationType("DO")
                .workerService("worker-service")
                .retryCount(0)
                .maxRetries(3)
                .orchestrationRun(run)
                .startedAt(LocalDateTime.now())
                .build();
    }
}

