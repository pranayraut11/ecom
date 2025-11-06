# Failure Handling and UNDO Flow Documentation

## Overview
This document explains how the orchestration system handles failures and triggers UNDO operations for completed steps.

## Failure Scenarios

### 1. Step Failure with Retry Exhaustion
When a step fails and all retries are exhausted:

**Flow:**
1. Worker executes DO operation
2. DO operation fails
3. System retries up to `maxRetries` times
4. After all retries exhausted:
   - Step status → `RETRY_EXHAUSTED`
   - Orchestration status → `FAILED`
   - System automatically triggers UNDO for all completed steps

**Implementation Location:** `DoOperationHandler.handleRetryExhausted()`

### 2. Step Failure with FAIL_STEP Action
When a worker explicitly signals a step failure (no retry needed):

**Flow:**
1. Worker executes DO operation
2. Worker determines operation should not be retried
3. Worker sends response with `action: FAIL_STEP`
4. System immediately:
   - Step status → `FAILED`
   - Orchestration status → `FAILED`
   - Triggers UNDO for all completed steps

**Implementation Location:** `UndoOperationHandler.handleFailResponse()`

## UNDO Process

### Sequential Orchestration UNDO
Steps are undone in **reverse order** of execution:

```
DO Order:    Step1 → Step2 → Step3 → Step4 (FAILED)
UNDO Order:  Step3 → Step2 → Step1
```

**Process:**
1. Find all steps with status `DO_SUCCESS`
2. Sort steps by sequence number in descending order
3. Start with highest sequence number (most recent)
4. Execute UNDO for each step sequentially
5. Wait for UNDO response before proceeding to next step
6. When all UNDOs complete → Orchestration status → `UNDONE`

### Parallel Orchestration UNDO
All successfully completed steps are undone **simultaneously**:

```
DO:    Step1 + Step2 + Step3 (parallel) → Step4 (FAILED)
UNDO:  Step1 + Step2 + Step3 (all triggered simultaneously)
```

**Process:**
1. Find all steps with status `DO_SUCCESS`
2. Trigger UNDO for all steps simultaneously
3. Wait for all UNDO responses
4. When all UNDOs complete → Orchestration status → `UNDONE`

## Status Flow Diagram

### Normal Execution
```
PENDING → IN_PROGRESS → DO_SUCCESS → (next step or COMPLETED)
```

### Failure with Retry
```
PENDING → IN_PROGRESS → (retry) → IN_PROGRESS → ... → RETRY_EXHAUSTED → FAILED → UNDOING
```

### Explicit Failure
```
PENDING → IN_PROGRESS → FAILED → UNDOING
```

### UNDO Flow
```
DO_SUCCESS → UNDOING → UNDO_SUCCESS → UNDONE (all steps)
           └─ (retry) → UNDOING → UNDO_FAIL (if retries exhausted)
```

## Message Format

### FAIL_STEP Action Message
Worker should send this when a step fails and should not be retried:

```json
{
  "payload": {...},
  "headers": {
    "flowId": "unique-flow-id",
    "stepName": "createRealm",
    "action": "FAIL_STEP",
    "status": false,
    "errorMessage": "Realm creation failed: duplicate realm name",
    "timestamp": "2025-11-03T10:30:00Z"
  }
}
```

### UNDO Action Message
Orchestrator sends this to worker for UNDO operation:

```json
{
  "payload": {...},
  "headers": {
    "flowId": "unique-flow-id",
    "stepName": "createRealm",
    "action": "UNDO",
    "seq": 1,
    "orchestrationName": "tenantCreation",
    "timestamp": "2025-11-03T10:31:00Z"
  }
}
```

### UNDO Response Message
Worker responds with UNDO result:

```json
{
  "payload": {...},
  "headers": {
    "flowId": "unique-flow-id",
    "stepName": "createRealm",
    "action": "UNDO",
    "status": true,  // true = UNDO success, false = UNDO failed
    "errorMessage": null,
    "timestamp": "2025-11-03T10:31:05Z"
  }
}
```

## Configuration Requirements

### Orchestration Registration (Initiator)
```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: initiator
    type: sequential
    steps:
      - seq: 1
        name: createRealm
        objectType: String
        doMethod: createRealm      # Required
        undoMethod: deleteRealm    # Required for UNDO support
```

### Worker Registration
```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: worker
    steps:
      - name: createRealm
        objectType: String
        handlerClass: realmService
        handlerMethod: createRealmByEvent      # DO handler
        undoHandlerMethod: deleteRealmByEvent  # UNDO handler
```

**Important:** Both `undoMethod` (initiator) and `undoHandlerMethod` (worker) are **required** for proper UNDO functionality.

## Worker Implementation Guidelines

### DO Handler Implementation
```java
@KafkaListener(topics = "orchestrator.tenant.createRealm.do")
public void createRealmByEvent(ExecutionMessage message) {
    try {
        // Extract data from message
        String tenantData = (String) message.getPayload();
        
        // Execute business logic
        Realm realm = createRealm(tenantData);
        
        // Send success response
        sendResponse(message, true, null, "DO");
        
    } catch (BusinessException e) {
        // Determine if retry is appropriate
        if (e.isRetryable()) {
            // Send failure - system will retry
            sendResponse(message, false, e.getMessage(), "DO");
        } else {
            // Send FAIL_STEP - no retry, immediate UNDO
            sendFailStepResponse(message, e.getMessage());
        }
    }
}
```

### UNDO Handler Implementation
```java
@KafkaListener(topics = "orchestrator.tenant.createRealm.undo")
public void deleteRealmByEvent(ExecutionMessage message) {
    try {
        // Extract data from message
        String tenantData = (String) message.getPayload();
        
        // Execute UNDO/rollback logic
        deleteRealm(tenantData);
        
        // Send UNDO success response
        sendResponse(message, true, null, "UNDO");
        
    } catch (Exception e) {
        // Send UNDO failure - system will retry
        sendResponse(message, false, e.getMessage(), "UNDO");
    }
}
```

### Response Helper Method
```java
private void sendResponse(ExecutionMessage message, boolean success, 
                         String errorMessage, String action) {
    Map<String, Object> headers = new HashMap<>(message.getHeaders());
    headers.put("status", success);
    headers.put("errorMessage", errorMessage);
    headers.put("action", action);
    
    ExecutionMessage response = new ExecutionMessage();
    response.setPayload(message.getPayload());
    response.setHeaders(headers);
    
    // Send to response topic
    kafkaTemplate.send("orchestrator." + orchName + ".response", response);
}

private void sendFailStepResponse(ExecutionMessage message, String errorMessage) {
    Map<String, Object> headers = new HashMap<>(message.getHeaders());
    headers.put("status", false);
    headers.put("errorMessage", errorMessage);
    headers.put("action", "FAIL_STEP");
    
    ExecutionMessage response = new ExecutionMessage();
    response.setPayload(message.getPayload());
    response.setHeaders(headers);
    
    kafkaTemplate.send("orchestrator." + orchName + ".response", response);
}
```

## Error Handling Best Practices

### When to Use FAIL_STEP vs Regular Failure

**Use FAIL_STEP when:**
- Business validation fails (e.g., duplicate record)
- External service returns unrecoverable error
- Data is invalid and cannot be processed
- Operation violates business rules

**Use Regular Failure (with retry) when:**
- Network timeout or temporary connectivity issue
- Database deadlock or temporary lock
- External service temporarily unavailable
- Rate limiting or throttling

### UNDO Implementation Tips

1. **Idempotent UNDO:** UNDO operations should be idempotent - running multiple times should be safe
2. **State Verification:** Check if resource exists before attempting to delete/undo
3. **Partial Success:** Handle cases where resource was partially created
4. **Logging:** Log all UNDO operations for audit trail
5. **Error Handling:** UNDO failures will be retried, but should not fail easily

### Example UNDO Implementation
```java
public void deleteRealmByEvent(ExecutionMessage message) {
    try {
        String realmId = extractRealmId(message);
        
        // Check if realm exists
        if (!realmExists(realmId)) {
            log.info("Realm already deleted or doesn't exist: {}", realmId);
            sendResponse(message, true, null, "UNDO");
            return;
        }
        
        // Perform deletion
        realmService.deleteRealm(realmId);
        log.info("Successfully deleted realm: {}", realmId);
        
        sendResponse(message, true, null, "UNDO");
        
    } catch (Exception e) {
        log.error("Failed to delete realm", e);
        sendResponse(message, false, e.getMessage(), "UNDO");
    }
}
```

## Monitoring and Troubleshooting

### Key Metrics to Monitor
- Step failure rate
- UNDO success rate
- Average time for UNDO completion
- Retry exhaustion frequency

### Log Messages to Watch For
- "Step marked as FAILED" - Indicates FAIL_STEP action received
- "Triggering UNDO for X successfully completed steps" - UNDO process started
- "UNDO operation retry exhausted" - Critical: UNDO failed completely
- "Orchestration UNDO completed" - All steps successfully rolled back

### Database Queries for Debugging

Find failed orchestrations:
```sql
SELECT * FROM orchestration_run WHERE status = 'FAILED';
```

Find orchestrations in UNDO process:
```sql
SELECT * FROM orchestration_run WHERE status = 'UNDOING';
```

Find steps that failed UNDO:
```sql
SELECT * FROM orchestration_step_run WHERE status = 'UNDO_FAIL';
```

## Summary

The `handleFailResponse` method provides a robust mechanism for handling explicit step failures and triggering compensating UNDO operations. The key flow is:

1. **Step fails** → Mark as `FAILED`
2. **Orchestration fails** → Mark as `FAILED`
3. **Find completed steps** → Filter by `DO_SUCCESS`
4. **Trigger UNDO** → Call `undoOrchestration()`
5. **UNDO completes** → Orchestration status → `UNDONE`

This ensures data consistency and proper rollback of partial operations in distributed orchestrations.

