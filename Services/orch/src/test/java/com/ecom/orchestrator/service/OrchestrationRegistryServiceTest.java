package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.messaging.interfaces.TopicManager;
import com.ecom.orchestrator.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class OrchestrationRegistryServiceTest {

    @Mock
    private OrchestrationTemplateRepository orchestrationTemplateRepository;

    @Mock
    private OrchestrationStepTemplateRepository stepTemplateRepository;

    @Mock
    private WorkerRegistrationRepository workerRegistrationRepository;

    @Mock
    private RegistrationAuditRepository registrationAuditRepository;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private TopicManager topicManager;

    @Mock
    private Serializer serializer;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrchestrationRegistryService registryService;

    private OrchestrationRegistrationDto initiatorRegistration;
    private OrchestrationRegistrationDto workerRegistration;

    @BeforeEach
    void setUp() {
        initiatorRegistration = new OrchestrationRegistrationDto();
        initiatorRegistration.setOrchName("tenantCreation");
        initiatorRegistration.setAs("initiator");
        initiatorRegistration.setType("sequential");
        initiatorRegistration.setSteps(List.of(
                new StepDefinitionDto(1, "createRealm", "String"),
                new StepDefinitionDto(2, "createClient", "String")
        ));

        workerRegistration = new OrchestrationRegistrationDto();
        workerRegistration.setOrchName("tenantCreation");
        workerRegistration.setAs("worker");
        workerRegistration.setSteps(List.of(
                new StepDefinitionDto(null, "createRealm", "String")
        ));
    }

    @Test
    void testRegisterInitiator_Success() throws Exception {
        // Given
        when(orchestrationTemplateRepository.existsByOrchName("tenantCreation")).thenReturn(false);
        when(orchestrationTemplateRepository.save(any(OrchestrationTemplate.class)))
                .thenReturn(createMockTemplate());
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(serializer.serialize(any())).thenReturn(new byte[0]);

        // When
        registryService.registerOrchestration(initiatorRegistration, "tenant-service");

        // Then
        verify(orchestrationTemplateRepository).save(any(OrchestrationTemplate.class));
        verify(stepTemplateRepository, times(2)).save(any(OrchestrationStepTemplate.class));
        verify(topicManager, times(2)).createTopic(anyString());
        verify(registrationAuditRepository).save(any(RegistrationAudit.class));
        verify(messagePublisher).send(eq("orchestrator.registration.status"), any(byte[].class));
    }

    @Test
    void testRegisterInitiator_AlreadyExists() throws Exception {
        // Given
        when(orchestrationTemplateRepository.existsByOrchName("tenantCreation")).thenReturn(true);
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(serializer.serialize(any())).thenReturn(new byte[0]);

        // When
        registryService.registerOrchestration(initiatorRegistration, "tenant-service");

        // Then
        verify(orchestrationTemplateRepository, never()).save(any(OrchestrationTemplate.class));
        verify(registrationAuditRepository).save(any(RegistrationAudit.class));
        verify(messagePublisher).send(eq("orchestrator.registration.status"), any(byte[].class));
    }

    @Test
    void testRegisterWorker_Success() throws Exception {
        // Given
        OrchestrationTemplate template = createMockTemplate();
        when(orchestrationTemplateRepository.findByOrchNameWithSteps("tenantCreation"))
                .thenReturn(Optional.of(template));
        when(workerRegistrationRepository.existsByOrchNameAndStepNameAndWorkerService(
                anyString(), anyString(), anyString())).thenReturn(false);
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(serializer.serialize(any())).thenReturn(new byte[0]);

        // When
        registryService.registerOrchestration(workerRegistration, "realm-service");

        // Then
        verify(workerRegistrationRepository).save(any(WorkerRegistration.class));
        verify(registrationAuditRepository).save(any(RegistrationAudit.class));
        verify(messagePublisher).send(eq("orchestrator.registration.status"), any(byte[].class));
    }

    private OrchestrationTemplate createMockTemplate() {
        OrchestrationTemplate template = OrchestrationTemplate.builder()
                .id(1L)
                .orchName("tenantCreation")
                .type(OrchestrationTypeEnum.SEQUENTIAL)
                .initiatorService("tenant-service")
                .status(OrchestrationStatusEnum.PENDING)
                .build();

        OrchestrationStepTemplate step1 = OrchestrationStepTemplate.builder()
                .id(1L)
                .stepName("createRealm")
                .seq(1)
                .objectType("String")
                .topicName("orchestrator.tenantCreation.createRealm")
                .template(template)
                .build();

        template.setSteps(List.of(step1));
        return template;
    }
}
