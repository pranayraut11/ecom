# Implementation Summary: handleFailResponse Method

## What Was Implemented

The `handleFailResponse` method in `UndoOperationHandler` has been properly implemented to handle explicit step failures (FAIL_STEP action) from workers.

## Implementation Details

### Location
- **File:** `/src/main/java/com/ecom/orchestrator/service/UndoOperationHandler.java`
- **Method:** `handleFailResponse(String flowId, String stepName, boolean success, String errorMessage, ExecutionMessage message)`

### Functionality

The method performs the following steps in order:

#### 1. Load Orchestration Data
```java
- Finds the orchestration run by flowId
- Finds the specific step run by flowId and stepName
- Returns early if either not found
```

#### 2. Mark Current Step as FAILED
```java
- Sets step status to ExecutionStatusEnum.FAILED
- Stores the error message
- Sets completion timestamp
- Saves step run to database
- Logs the failure
```

#### 3. Mark Orchestration as FAILED
```java
- Sets orchestration run status to ExecutionStatusEnum.FAILED
- Saves orchestration run to database
- Logs the orchestration failure
```

#### 4. Trigger UNDO for Completed Steps
```java
- Finds all steps with status DO_SUCCESS
- If completed steps exist:
  * Logs count of steps to undo
  * Calls undoOrchestration() to start UNDO process
- If no completed steps:
  * Sets completion timestamp
  * Saves final orchestration state
  * Logs that orchestration failed with nothing to undo
```

## Key Features

### ✅ Proper Status Management
- Current failed step → `FAILED`
- Orchestration → `FAILED`
- Completed steps → Will be `UNDOING` → `UNDO_SUCCESS` or `UNDO_FAIL`

### ✅ Transaction Safety
- Method is marked with `@Transactional`
- Ensures all database updates succeed or rollback together

### ✅ Comprehensive Logging
- Logs when FAIL_STEP action is received
- Logs step marked as FAILED
- Logs orchestration marked as FAILED
- Logs count of steps being undone
- Logs if no steps need to be undone

### ✅ UNDO Triggering
- Automatically calls `undoOrchestration()` method
- `undoOrchestration()` handles:
  - Sequential orchestrations: UNDO in reverse order
  - Parallel orchestrations: UNDO all simultaneously
  - Retry logic for failed UNDOs
  - Final status updates

## Flow Diagram

```
Worker sends FAIL_STEP
        ↓
handleFailResponse()
        ↓
┌───────────────────────────┐
│ Step Status = FAILED      │
│ + error message           │
│ + timestamp               │
└───────────────────────────┘
        ↓
┌───────────────────────────┐
│ Orchestration = FAILED    │
└───────────────────────────┘
        ↓
┌───────────────────────────┐
│ Find DO_SUCCESS steps     │
└───────────────────────────┘
        ↓
    ┌───┴───┐
    │ Any?  │
    └───┬───┘
        │
    Yes │           No
        ↓            ↓
┌───────────────┐  ┌────────────────┐
│undoOrchestra- │  │Set completed   │
│tion(flowId)   │  │timestamp       │
└───────────────┘  └────────────────┘
        ↓                   ↓
┌───────────────┐    ┌────────────┐
│Sequential:    │    │    DONE    │
│Reverse order  │    └────────────┘
│               │
│Parallel:      │
│All together   │
└───────────────┘
        ↓
┌───────────────┐
│Send UNDO msgs │
│to workers     │
└───────────────┘
        ↓
┌───────────────┐
│Wait for UNDO  │
│responses      │
└───────────────┘
        ↓
┌───────────────┐
│Final Status:  │
│UNDONE or      │
│FAILED         │
└───────────────┘
```

## Usage in OrchestrationMessageHandler

The method is called from `handleStepResponseMessage()`:

```java
if ("FAIL_STEP".equalsIgnoreCase(action)) {
    log.info("Handling FAIL_STEP action for flowId={}, stepName={}", flowId, stepName);
    undoOperationHandler.handleFailResponse(flowId, stepName, success, errorMessage, message);
}
```

## Worker Integration

Workers should send FAIL_STEP action when:
- Business validation fails
- Unrecoverable errors occur
- No retry would help

### Example Message from Worker:
```json
{
  "payload": {...},
  "headers": {
    "flowId": "abc-123",
    "stepName": "createRealm",
    "action": "FAIL_STEP",
    "status": false,
    "errorMessage": "Realm already exists: tenant-xyz"
  }
}
```

## Configuration Example

### Initiator Configuration
```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: initiator
    type: sequential
    steps:
      - seq: 1
        name: createRealm
        objectType: String
        doMethod: createRealm
        undoMethod: deleteRealm    # Required for UNDO
```

### Worker Configuration
```yaml
orchestrations:
  - orchestrationName: tenantCreation
    as: worker
    steps:
      - name: createRealm
        objectType: String
        handlerClass: realmService
        handlerMethod: createRealmByEvent
        undoHandlerMethod: deleteRealmByEvent  # Required for UNDO
```

## Testing Scenario

### Sequential Orchestration: tenantCreation
```
Steps: createRealm → createClient → assignPermissions

Execution:
1. createRealm → SUCCESS (DO_SUCCESS)
2. createClient → SUCCESS (DO_SUCCESS)
3. assignPermissions → FAIL_STEP

Result:
1. assignPermissions status → FAILED
2. Orchestration status → FAILED
3. UNDO triggered for:
   - createClient (UNDO deleteClient)
   - createRealm (UNDO deleteRealm)
4. After all UNDOs complete:
   - Orchestration status → UNDONE
```

## Related Documentation

- **Detailed Flow Guide:** `FAILURE_HANDLING_GUIDE.md`
- **Configuration Examples:** `examples/orchestration-complete-example.yaml`
- **DO-UNDO Documentation:** `DO-UNDO-ORCHESTRATION.md`

## Summary

The `handleFailResponse` method is now fully implemented to:
1. ✅ Store current step status as **FAILED**
2. ✅ Store orchestration status as **FAILED**
3. ✅ Call UNDO for all successfully completed steps
4. ✅ Handle both sequential and parallel orchestrations
5. ✅ Provide comprehensive logging for debugging
6. ✅ Maintain transaction integrity

The implementation ensures proper rollback of distributed operations when any step fails, maintaining data consistency across multiple services.

