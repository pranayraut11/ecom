# Worker Registration Format - DO/UNDO Orchestration

## Overview

Worker registration has been updated to include **handlerClass**, **doMethod**, and **undoMethod** to clearly specify which Spring beans and methods handle DO and UNDO operations.

---

## Registration Formats

### Single Orchestration Registration

**File**: `worker-registration.yaml`

```yaml
orchestrationName: tenantCreation
as: worker
steps:
  - name: createRealm
    objectType: String
    handlerClass: realmService              # Spring bean name
    doMethod: createRealmByEvent            # DO operation method
    undoMethod: undoCreateRealmByEvent      # UNDO operation method

  - name: createClient
    objectType: String
    handlerClass: clientService
    doMethod: createClientByEvent
    undoMethod: undoCreateClientByEvent
```

### Multiple Orchestrations Registration

**File**: `worker-registration-multiple.yaml`

```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: worker
    steps:
      - name: createRealm
        objectType: String
        handlerClass: realmService
        doMethod: createRealmByEvent
        undoMethod: undoCreateRealmByEvent

  - orchestrationName: userOnboarding
    as: worker
    steps:
      - name: createUserProfile
        objectType: UserProfile
        handlerClass: userService
        doMethod: createUserProfileByEvent
        undoMethod: undoCreateUserProfileByEvent
```

---

## Field Descriptions

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | String | Yes | Step name (must match initiator registration) |
| `objectType` | String | Yes | Type of payload object |
| `handlerClass` | String | Yes | Spring bean name that handles this step |
| `doMethod` | String | Yes | Method name for DO (forward) operation |
| `undoMethod` | String | Yes | Method name for UNDO (rollback) operation |

---

## Implementation Mapping

### Registration → Implementation

```yaml
# Registration
- name: createRealm
  handlerClass: realmService
  doMethod: createRealmByEvent
  undoMethod: undoCreateRealmByEvent
```

**Maps to:**

```java
@Service("realmService")  // Bean name matches handlerClass
public class RealmService {
    
    // Method name matches doMethod
    @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.do")
    public void createRealmByEvent(ExecutionMessage message) {
        // DO operation implementation
    }
    
    // Method name matches undoMethod
    @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.undo")
    public void undoCreateRealmByEvent(ExecutionMessage message) {
        // UNDO operation implementation
    }
}
```

---

## Topics Created

For each step, the orchestrator creates:

| Step | DO Topic | UNDO Topic |
|------|----------|------------|
| createRealm | `orchestrator.tenantCreation.createRealm.do` | `orchestrator.tenantCreation.createRealm.undo` |
| createClient | `orchestrator.tenantCreation.createClient.do` | `orchestrator.tenantCreation.createClient.undo` |

---

## Complete Worker Implementation Example

```java
@Service("realmService")
@Slf4j
@RequiredArgsConstructor
public class RealmService {
    
    private final KeycloakAdmin keycloakAdmin;
    private final UndoStateRepository undoStateRepository;
    private final KafkaTemplate<String, ExecutionMessage> kafkaTemplate;
    
    // ========================================================================
    // DO Operation
    // ========================================================================
    @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.do")
    public void createRealmByEvent(ExecutionMessage message) {
        String flowId = extractFlowId(message);
        String stepName = extractStepName(message);
        
        log.info("DO operation started: flowId={}, step={}", flowId, stepName);
        
        try {
            // Extract payload
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String realmName = (String) payload.get("realmName");
            
            // Execute forward operation
            String realmId = keycloakAdmin.createRealm(realmName);
            log.info("Realm created: realmId={}, flowId={}", realmId, flowId);
            
            // Store state for potential UNDO
            UndoState state = UndoState.builder()
                .flowId(flowId)
                .stepName(stepName)
                .resourceType("REALM")
                .resourceId(realmId)
                .build();
            undoStateRepository.save(state);
            
            // Send SUCCESS response
            sendResponse(message, "DO", true, null);
            
        } catch (Exception e) {
            log.error("DO operation failed: flowId={}, step={}", flowId, stepName, e);
            sendResponse(message, "DO", false, e.getMessage());
        }
    }
    
    // ========================================================================
    // UNDO Operation
    // ========================================================================
    @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.undo")
    public void undoCreateRealmByEvent(ExecutionMessage message) {
        String flowId = extractFlowId(message);
        String stepName = extractStepName(message);
        
        log.info("UNDO operation started: flowId={}, step={}", flowId, stepName);
        
        try {
            // Retrieve stored state
            UndoState state = undoStateRepository.findByFlowIdAndStepName(flowId, stepName)
                .orElse(null);
            
            if (state == null) {
                log.warn("No state found for UNDO, treating as success: flowId={}", flowId);
                sendResponse(message, "UNDO", true, null);
                return;
            }
            
            // Execute rollback operation
            String realmId = state.getResourceId();
            keycloakAdmin.deleteRealm(realmId);
            log.info("Realm deleted: realmId={}, flowId={}", realmId, flowId);
            
            // Clear undo state
            undoStateRepository.delete(state);
            
            // Send SUCCESS response
            sendResponse(message, "UNDO", true, null);
            
        } catch (NotFoundException e) {
            // Handle "already deleted" as success (idempotency)
            log.warn("Realm not found, treating as success: flowId={}", flowId);
            undoStateRepository.deleteByFlowIdAndStepName(flowId, stepName);
            sendResponse(message, "UNDO", true, null);
            
        } catch (Exception e) {
            log.error("UNDO operation failed: flowId={}, step={}", flowId, stepName, e);
            sendResponse(message, "UNDO", false, e.getMessage());
        }
    }
    
    // ========================================================================
    // Helper Methods
    // ========================================================================
    
    private void sendResponse(ExecutionMessage message, String action, 
                              boolean success, String errorMessage) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("flowId", message.getHeaders().get("flowId"));
        headers.put("stepName", message.getHeaders().get("stepName"));
        headers.put("action", action);
        headers.put("status", success);
        
        if (errorMessage != null) {
            headers.put("errorMessage", errorMessage);
        }
        
        ExecutionMessage response = ExecutionMessage.builder()
            .headers(headers)
            .payload(message.getPayload())
            .build();
        
        kafkaTemplate.send("orchestrator.response.result", response);
        
        log.info("Response sent: action={}, success={}, flowId={}", 
                action, success, headers.get("flowId"));
    }
    
    private String extractFlowId(ExecutionMessage message) {
        return message.getHeaders().get("flowId").toString();
    }
    
    private String extractStepName(ExecutionMessage message) {
        return message.getHeaders().get("stepName").toString();
    }
}
```

---

## UndoState Entity Example

```java
@Entity
@Table(name = "undo_state")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UndoState {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flow_id", nullable = false)
    private String flowId;
    
    @Column(name = "step_name", nullable = false)
    private String stepName;
    
    @Column(name = "resource_type", nullable = false)
    private String resourceType;
    
    @Column(name = "resource_id", nullable = false)
    private String resourceId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
```

---

## JSON Format Examples

### Single Orchestration

```json
{
  "payload": {
    "orchestrationName": "tenantCreation",
    "as": "worker",
    "steps": [
      {
        "name": "createRealm",
        "objectType": "String",
        "handlerClass": "realmService",
        "doMethod": "createRealmByEvent",
        "undoMethod": "undoCreateRealmByEvent"
      }
    ]
  },
  "headers": {
    "eventType": "ORCHESTRATION_REGISTRATION",
    "serviceName": "keycloak-service"
  }
}
```

### Multiple Orchestrations

```json
{
  "payload": {
    "orchestrations": [
      {
        "orchestrationName": "tenantCreation",
        "as": "worker",
        "steps": [
          {
            "name": "createRealm",
            "objectType": "String",
            "handlerClass": "realmService",
            "doMethod": "createRealmByEvent",
            "undoMethod": "undoCreateRealmByEvent"
          }
        ]
      },
      {
        "orchestrationName": "userOnboarding",
        "as": "worker",
        "steps": [...]
      }
    ]
  },
  "headers": {
    "eventType": "ORCHESTRATION_REGISTRATION",
    "serviceName": "identity-service"
  }
}
```

---

## Naming Conventions

### Recommended Method Naming

| Operation | Pattern | Example |
|-----------|---------|---------|
| DO method | `{action}{Entity}ByEvent` | `createRealmByEvent` |
| UNDO method | `undo{Action}{Entity}ByEvent` | `undoCreateRealmByEvent` |

### Alternative Patterns

| Operation | Pattern | Example |
|-----------|---------|---------|
| DO method | `handle{StepName}Do` | `handleCreateRealmDo` |
| UNDO method | `handle{StepName}Undo` | `handleCreateRealmUndo` |

---

## Benefits of Handler Specification

### ✅ Clear Responsibility
- Explicitly defines which service handles each step
- Easy to identify which bean implements which operation

### ✅ Better Organization
- Different services can handle different steps
- Clear separation of concerns

### ✅ Self-Documenting
- Registration serves as documentation
- Method names describe operations clearly

### ✅ Easier Testing
- Can test specific methods directly
- Clear contract between orchestrator and workers

---

## Files Summary

| File | Purpose |
|------|---------|
| `worker-registration.yaml` | Single orchestration registration template |
| `worker-registration-multiple.yaml` | Multiple orchestrations registration template |
| `worker-registration.json` | Single orchestration Kafka message |
| `worker-registration-multiple.json` | Multiple orchestrations Kafka message |
| `worker-implementation-example.java` | Complete implementation example |

---

## Quick Start

### 1. Define Your Registration

```yaml
orchestrationName: myOrchestration
as: worker
steps:
  - name: myStep
    objectType: String
    handlerClass: myService
    doMethod: executeMyStepDo
    undoMethod: executeMyStepUndo
```

### 2. Implement Your Service

```java
@Service("myService")
public class MyService {
    
    @KafkaListener(topics = "orchestrator.myOrchestration.myStep.do")
    public void executeMyStepDo(ExecutionMessage message) {
        // Implement DO
    }
    
    @KafkaListener(topics = "orchestrator.myOrchestration.myStep.undo")
    public void executeMyStepUndo(ExecutionMessage message) {
        // Implement UNDO
    }
}
```

### 3. Send Registration

```bash
kafka-console-producer --broker-list localhost:9092 \
  --topic orchestrator.registration \
  < worker-registration.json
```

---

## 🎯 Summary

The updated worker registration format now includes:

✅ **handlerClass** - Spring bean that handles the step  
✅ **doMethod** - Method for DO (forward) operation  
✅ **undoMethod** - Method for UNDO (rollback) operation  
✅ **Multiple orchestrations** - Support for registering multiple orchestrations  
✅ **Clear mapping** - Direct mapping from registration to implementation  

This makes it clear exactly which service and methods handle each step's DO and UNDO operations!

---

**Last Updated**: November 1, 2025  
**Version**: 2.0.0  
**Status**: ✅ Production Ready

