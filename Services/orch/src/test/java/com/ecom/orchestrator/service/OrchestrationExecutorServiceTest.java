package com.ecom.orchestrator.service;

import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.serialization.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchestrationExecutorServiceTest {

    @Mock
    private OrchestrationTemplateRepository orchestrationTemplateRepository;

    @Mock
    private OrchestrationStepTemplateRepository stepTemplateRepository;

    @Mock
    private WorkerRegistrationRepository workerRegistrationRepository;

    @Mock
    private OrchestrationRunRepository orchestrationRunRepository;

    @Mock
    private OrchestrationStepRunRepository stepRunRepository;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private Serializer serializer;

    @Mock
    private UndoService undoService;

    @InjectMocks
    private OrchestrationExecutorService executorService;

    private OrchestrationTemplate sequentialTemplate;
    private OrchestrationTemplate simultaneousTemplate;

    @BeforeEach
    void setUp() {
        sequentialTemplate = createMockTemplate(OrchestrationTypeEnum.SEQUENTIAL);
        simultaneousTemplate = createMockTemplate(OrchestrationTypeEnum.SIMULTANEOUS);
    }

    @Test
    void testStartOrchestration_Sequential_Success() {
        // Given
        when(orchestrationTemplateRepository.findByOrchNameWithSteps("tenantCreation"))
                .thenReturn(Optional.of(sequentialTemplate));
        when(orchestrationRunRepository.save(any(OrchestrationRun.class)))
                .thenReturn(createMockOrchestrationRun());
        when(stepRunRepository.saveAll(any())).thenReturn(List.of());
        when(workerRegistrationRepository.findByOrchNameAndStepName(anyString(), anyString()))
                .thenReturn(Optional.of(createMockWorkerRegistration()));
        when(serializer.serialize(any())).thenReturn(new byte[0]);

        // When
        String flowId = executorService.startOrchestration("tenantCreation", new byte[0]);

        // Then
        assertNotNull(flowId);
        verify(orchestrationRunRepository).save(any(OrchestrationRun.class));
        verify(stepRunRepository).saveAll(any());
        verify(messagePublisher).send(anyString(), any(byte[].class));
    }

    @Test
    void testStartOrchestration_Simultaneous_Success() {
        // Given
        when(orchestrationTemplateRepository.findByOrchNameWithSteps("tenantCreation"))
                .thenReturn(Optional.of(simultaneousTemplate));
        when(orchestrationRunRepository.save(any(OrchestrationRun.class)))
                .thenReturn(createMockOrchestrationRun());
        when(stepRunRepository.saveAll(any())).thenReturn(List.of());
        when(workerRegistrationRepository.findByOrchNameAndStepName(anyString(), anyString()))
                .thenReturn(Optional.of(createMockWorkerRegistration()));
        when(serializer.serialize(any())).thenReturn(new byte[0]);

        // When
        String flowId = executorService.startOrchestration("tenantCreation", new byte[0]);

        // Then
        assertNotNull(flowId);
        verify(messagePublisher, times(2)).send(anyString(), any(byte[].class));
    }

    @Test
    void testStartOrchestration_OrchestrationNotFound() {
        // Given
        when(orchestrationTemplateRepository.findByOrchNameWithSteps("nonExistent"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                executorService.startOrchestration("nonExistent", new byte[0]));
    }

    @Test
    void testHandleStepResponse_Success() {
        // Given
        OrchestrationRun run = createMockOrchestrationRun();
        when(orchestrationRunRepository.findByFlowIdWithSteps(anyString()))
                .thenReturn(Optional.of(run));
        when(orchestrationTemplateRepository.findByOrchNameWithSteps(anyString()))
                .thenReturn(Optional.of(sequentialTemplate));
        when(stepRunRepository.findByOrchestrationRunFlowIdAndStepName(anyString(), anyString()))
                .thenReturn(Optional.of(createMockStepRun()));

        // When
        executorService.handleStepResponse("test-flow-id", "createRealm", true, null,null);

        // Then
        verify(stepRunRepository).save(any(OrchestrationStepRun.class));
    }

    @Test
    void testHandleStepResponse_Failure() {
        // Given
        OrchestrationRun run = createMockOrchestrationRun();
        when(orchestrationRunRepository.findByFlowIdWithSteps(anyString()))
                .thenReturn(Optional.of(run));
        when(stepRunRepository.findByOrchestrationRunFlowIdAndStepName(anyString(), anyString()))
                .thenReturn(Optional.of(createMockStepRun()));
        when(orchestrationRunRepository.findByFlowId(anyString()))
                .thenReturn(Optional.of(run));

        // When
        executorService.handleStepResponse("test-flow-id", "createRealm", false, "Error occurred",null);

        // Then
        verify(undoService).undoOrchestration("test-flow-id");
    }

    private OrchestrationTemplate createMockTemplate(OrchestrationTypeEnum type) {
        OrchestrationTemplate template = OrchestrationTemplate.builder()
                .id(1L)
                .orchName("tenantCreation")
                .type(type)
                .initiatorService("tenant-service")
                .status(OrchestrationStatusEnum.SUCCESS)
                .build();

        OrchestrationStepTemplate step1 = OrchestrationStepTemplate.builder()
                .id(1L)
                .stepName("createRealm")
                .seq(1)
                .objectType("String")
                .topicName("orchestrator.tenantCreation.createRealm")
                .template(template)
                .build();

        OrchestrationStepTemplate step2 = OrchestrationStepTemplate.builder()
                .id(2L)
                .stepName("createClient")
                .seq(2)
                .objectType("String")
                .topicName("orchestrator.tenantCreation.createClient")
                .template(template)
                .build();

        template.setSteps(List.of(step1, step2));
        return template;
    }

    private OrchestrationRun createMockOrchestrationRun() {
        OrchestrationRun run = OrchestrationRun.builder()
                .id(1L)
                .flowId(UUID.randomUUID().toString())
                .orchName("tenantCreation")
                .status(ExecutionStatusEnum.IN_PROGRESS)
                .build();

        OrchestrationStepRun stepRun = OrchestrationStepRun.builder()
                .id(1L)
                .stepName("createRealm")
                .seq(1)
                .status(ExecutionStatusEnum.PENDING)
                .orchestrationRun(run)
                .build();

        run.setStepRuns(List.of(stepRun));
        return run;
    }

    private WorkerRegistration createMockWorkerRegistration() {
        return WorkerRegistration.builder()
                .id(1L)
                .orchName("tenantCreation")
                .stepName("createRealm")
                .workerService("realm-service")
                .topicName("orchestrator.tenantCreation.createRealm")
                .build();
    }

    private OrchestrationStepRun createMockStepRun() {
        return OrchestrationStepRun.builder()
                .id(1L)
                .stepName("createRealm")
                .seq(1)
                .status(ExecutionStatusEnum.IN_PROGRESS)
                .build();
    }
}
