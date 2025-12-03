package com.ecom.orchestrator.mapper;

import com.ecom.orchestrator.constant.RegistrationConstants;
import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.dto.RegistrationStatusEventDto;
import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.entity.*;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrchestrationMapper {

    /**
     * Map OrchestrationRegistrationDto to new OrchestrationTemplate entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orchName", source = "dto.orchestrationName")
    @Mapping(target = "type", source = "dto.type", qualifiedByName = "stringToOrchestrationTypeEnum")
    @Mapping(target = "initiatorService", source = "serviceName")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "steps", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrchestrationTemplate toOrchestrationTemplate(OrchestrationRegistrationDto dto, String serviceName);

    /**
     * Update existing OrchestrationTemplate from RegistrationDto
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orchName", ignore = true)
    @Mapping(target = "type", source = "dto.type", qualifiedByName = "stringToOrchestrationTypeEnum")
    @Mapping(target = "initiatorService", source = "serviceName")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "steps", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateOrchestrationTemplate(@MappingTarget OrchestrationTemplate template,
                                     OrchestrationRegistrationDto dto,
                                     String serviceName);

    /**
     * Map StepDefinitionDto to OrchestrationStepTemplate entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", source = "template")
    @Mapping(target = "seq", source = "stepDto.seq")
    @Mapping(target = "stepName", source = "stepDto.name")
    @Mapping(target = "objectType", source = "stepDto.objectType")
    @Mapping(target = "topicName", expression = "java(generateTopicName(orchName, stepDto.getName()))")
    @Mapping(target = "doTopic", expression = "java(generateDoTopicName(orchName, stepDto.getName()))")
    @Mapping(target = "undoTopic", expression = "java(generateUndoTopicName(orchName, stepDto.getName()))")
    @Mapping(target = "maxRetries", constant = "3")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "sharedTopic", source = "sharedTopic")
    OrchestrationStepTemplate toStepTemplate(StepDefinitionDto stepDto,
                                            OrchestrationTemplate template,
                                            String orchName,boolean sharedTopic);

    /**
     * Map worker registration data to WorkerRegistration entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orchName", source = "orchName")
    @Mapping(target = "stepName", source = "stepName")
    @Mapping(target = "workerService", source = "serviceName")
    @Mapping(target = "topicName", source = "topicName")
    WorkerRegistration toWorkerRegistration(String orchName,
                                           String stepName,
                                           String serviceName,
                                           String topicName);

    /**
     * Map registration data to RegistrationAudit entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orchName", source = "orchName")
    @Mapping(target = "asRole", source = "role")
    @Mapping(target = "serviceName", source = "serviceName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "registeredSteps", expression = "java(createRegisteredStepsMap(steps))")
    @Mapping(target = "failedSteps", expression = "java(createFailedStepsMap(failedSteps))")
    RegistrationAudit toRegistrationAudit(String orchName,
                                         RegistrationRoleEnum role,
                                         String serviceName,
                                         RegistrationStatusEnum status,
                                         List<StepDefinitionDto> steps,
                                         List<String> failedSteps);

    /**
     * Map registration status to RegistrationStatusEventDto
     */
    @Mapping(target = "eventType", expression = "java(com.ecom.orchestrator.constant.RegistrationConstants.EVENT_TYPE_REGISTRATION_STATUS)")
    @Mapping(target = "orchName", source = "orchName")
    @Mapping(target = "as", source = "asRole")
    @Mapping(target = "serviceName", source = "serviceName")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusEnumToString")
    @Mapping(target = "failureReason", expression = "java(createFailureReasonMap(failedSteps))")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    RegistrationStatusEventDto toRegistrationStatusEvent(String orchName,
                                                        String asRole,
                                                        String serviceName,
                                                        RegistrationStatusEnum status,
                                                        List<String> failedSteps);

    /**
     * Map RegistrationStatusEventDto to ExecutionMessage
     */
    @Mapping(target = "payload", source = "event")
    @Mapping(target = "headers", expression = "java(createMessageHeaders(orchName, asRole, serviceName, status))")
    ExecutionMessage toExecutionMessage(RegistrationStatusEventDto event,
                                       String orchName,
                                       String asRole,
                                       String serviceName,
                                       RegistrationStatusEnum status);

    // ==================== NAMED MAPPING METHODS ====================

    @Named("stringToOrchestrationTypeEnum")
    default OrchestrationTypeEnum stringToOrchestrationTypeEnum(String type) {
        return type != null ? OrchestrationTypeEnum.valueOf(type.toUpperCase()) : null;
    }

    @Named("statusEnumToString")
    default String statusEnumToString(RegistrationStatusEnum status) {
        return status != null ? status.name() : null;
    }

    // ==================== HELPER METHODS ====================

    default String generateTopicName(String orchName, String stepName) {
        return String.format("orchestrator.%s.%s", orchName, stepName);
    }

    default String generateDoTopicName(String orchName, String stepName) {
        return String.format("orchestrator.%s.%s.do", orchName, stepName);
    }

    default String generateUndoTopicName(String orchName, String stepName) {
        return String.format("orchestrator.%s.%s.undo", orchName, stepName);
    }

    default Map<String, Object> createRegisteredStepsMap(List<StepDefinitionDto> steps) {
        Map<String, Object> registeredStepsMap = new HashMap<>();
        registeredStepsMap.put("steps", steps);
        return registeredStepsMap;
    }

    default Map<String, Object> createFailedStepsMap(List<String> failedSteps) {
        if (failedSteps == null || failedSteps.isEmpty()) {
            return null;
        }
        Map<String, Object> failedStepsMap = new HashMap<>();
        failedStepsMap.put("errors", failedSteps);
        return failedStepsMap;
    }

    default Map<String, String> createFailureReasonMap(List<String> failedSteps) {
        if (failedSteps == null || failedSteps.isEmpty()) {
            return null;
        }
        return failedSteps.stream().collect(Collectors.toMap(
                failure -> "error_" + failedSteps.indexOf(failure),
                failure -> failure));
    }

    default Map<String, Object> createMessageHeaders(String orchName,
                                                     String asRole,
                                                     String serviceName,
                                                     RegistrationStatusEnum status) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("eventType", RegistrationConstants.EVENT_TYPE_REGISTRATION_STATUS);
        headers.put("orchName", orchName);
        headers.put("asRole", asRole);
        headers.put("serviceName", serviceName);
        headers.put("status", status.name());
        return headers;
    }
}

