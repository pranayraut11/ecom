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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UndoServiceTest {

    @Mock
    private OrchestrationRunRepository orchestrationRunRepository;

    @Mock
    private OrchestrationStepRunRepository stepRunRepository;

    @Mock
    private OrchestrationStepTemplateRepository stepTemplateRepository;

    @Mock
    private WorkerRegistrationRepository workerRegistrationRepository;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private Serializer serializer;

    @InjectMocks
    private UndoService undoService;

    @Test
    void testUndoOrchestration_Success() {
        // Given
        OrchestrationRun run = createMockOrchestrationRun();
        when(orchestrationRunRepository.findByFlowIdWithSteps("test-flow-id"))
                .thenReturn(Optional.of(run));
        when(stepTemplateRepository.findByTemplateOrchNameAndStepName(anyString(), anyString()))
                .thenReturn(Optional.of(createMockStepTemplate()));
        when(workerRegistrationRepository.findByOrchNameAndStepName(anyString(), anyString()))
                .thenReturn(Optional.of(createMockWorkerRegistration()));
        when(serializer.serialize(any())).thenReturn(new byte[0]);

        // When
        undoService.undoOrchestration("test-flow-id");

        // Then
        verify(orchestrationRunRepository).save(any(OrchestrationRun.class));
        //verify(messagePublisher).send(anyString(), any(byte[].class));
    }

    @Test
    void testHandleUndoResponse_Success() {
        // Given
        OrchestrationStepRun stepRun = createMockStepRun();
        when(stepRunRepository.findByOrchestrationRunFlowIdAndStepName("test-flow-id", "createRealm"))
                .thenReturn(Optional.of(stepRun));
        when(orchestrationRunRepository.findByFlowIdWithSteps("test-flow-id"))
                .thenReturn(Optional.of(createMockOrchestrationRun()));

        // When
        undoService.handleUndoResponse("test-flow-id", "createRealm", true, null);

        // Then
        verify(stepRunRepository).save(any(OrchestrationStepRun.class));
    }

    private OrchestrationRun createMockOrchestrationRun() {
        OrchestrationRun run = OrchestrationRun.builder()
                .id(1L)
                .flowId("test-flow-id")
                .orchName("tenantCreation")
                .status(ExecutionStatusEnum.FAILED)
                .build();

        OrchestrationStepRun completedStep = OrchestrationStepRun.builder()
                .id(1L)
                .stepName("createRealm")
                .seq(1)
                .status(ExecutionStatusEnum.COMPLETED)
                .orchestrationRun(run)
                .build();

        run.setStepRuns(List.of(completedStep));
        return run;
    }

    private OrchestrationStepTemplate createMockStepTemplate() {
        OrchestrationTemplate template = new OrchestrationTemplate();
        template.setInitiatorService("tenant-service");

        return OrchestrationStepTemplate.builder()
                .id(1L)
                .stepName("createRealm")
                .topicName("orchestrator.tenantCreation.createRealm")
                .template(template)
                .build();
    }

    private WorkerRegistration createMockWorkerRegistration() {
        return WorkerRegistration.builder()
                .id(1L)
                .orchName("tenantCreation")
                .stepName("createRealm")
                .workerService("realm-service")
                .build();
    }

    private OrchestrationStepRun createMockStepRun() {
        return OrchestrationStepRun.builder()
                .id(1L)
                .stepName("createRealm")
                .seq(1)
                .status(ExecutionStatusEnum.COMPLETED)
                .build();
    }
}
