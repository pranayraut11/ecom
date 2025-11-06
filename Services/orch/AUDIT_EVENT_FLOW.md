# 🧩 Audit Event Points in Orchestration Flow

## Complete Audit Event Lifecycle

This document describes **when** each audit event is triggered in the orchestration flow.

---

## 1️⃣ Orchestration-Level Events

### Phase: Start
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **ORCHESTRATION_STARTED** | When orchestration begins execution | `OrchestrationExecutorService.startOrchestration()` | `{"initiator": "tenant-management-service", "message": "Orchestration execution started"}` |

**Code Location:**
```java
// OrchestrationExecutorService.java
auditService.recordOrchestrationStart(flowId, orchName, initiator);
```

---

### Phase: Completion
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **ORCHESTRATION_COMPLETED** | When orchestration completes successfully (all steps done) | `DoOperationHandler.completeOrchestration()` | `{"durationMs": 15000, "message": "Orchestration execution completed"}` |

**Code Location:**
```java
// DoOperationHandler.java - completeOrchestration()
auditService.recordOrchestrationComplete(flowId, orchName, "SUCCESS", durationMs);
```

---

### Phase: Failure
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **ORCHESTRATION_FAILED** | When orchestration fails (step exhausted retries) | `DoOperationHandler.handleRetryExhausted()` | `{"reason": "Step createClient failed after retry exhaustion"}` |

**Code Location:**
```java
// DoOperationHandler.java - handleRetryExhausted()
auditService.recordOrchestrationFailure(flowId, orchName, reason);
```

---

### Phase: Rollback Start
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **ROLLBACK_STARTED** | When rollback process is initiated | `UndoOperationHandler.undoOrchestration()` | `{"message": "Rollback process started", "stepsToRollback": 3}` |

**Code Location:**
```java
// UndoOperationHandler.java - undoOrchestration()
auditService.recordRollbackStarted(flowId, orchName, reason, stepsToUndo.size());
```

---

### Phase: Rollback Completion
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **ROLLBACK_COMPLETED** | When rollback finishes | `UndoOperationHandler.completeUndoProcess()` | `{"message": "All steps rolled back successfully", "rolledBackSteps": 3}` |

**Code Location:**
```java
// UndoOperationHandler.java - completeUndoProcess()
auditService.recordRollbackComplete(flowId, orchName, rolledBackCount);
```

---

## 2️⃣ Step-Level Events (DO Operations)

### Phase: Step Start
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **STEP_STARTED** | When step execution begins (DO message sent) | `DoOperationHandler.sendDoMessage()` | `{"worker": "realm-service", "message": "Step execution started"}` |

**Code Location:**
```java
// DoOperationHandler.java - sendDoMessage()
auditService.recordStepStart(flowId, orchName, stepName, workerService, "DO");
```

---

### Phase: Step Success
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **STEP_COMPLETED** | When step completes successfully | `DoOperationHandler.handleDoSuccess()` | `{"worker": "realm-service", "durationMs": 5000, "message": "Step completed successfully"}` |

**Code Location:**
```java
// DoOperationHandler.java - handleDoSuccess()
auditService.recordStepSuccess(flowId, orchName, stepName, workerService, durationMs, "DO");
```

---

### Phase: Step Failure
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **STEP_FAILED** | When step execution fails | `DoOperationHandler.handleDoFailure()` | `{"worker": "client-service", "retryCount": 0, "message": "Step execution failed"}` |

**Code Location:**
```java
// DoOperationHandler.java - handleDoFailure()
auditService.recordStepFailure(flowId, orchName, stepName, workerService, errorMessage, retryCount, "DO");
```

---

## 3️⃣ Retry Events

### Phase: Retry Attempt
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **RETRY_ATTEMPT** | When step is being retried | `DoOperationHandler.retryDoOperation()` | `{"retryCount": 2, "maxRetries": 3, "message": "Retry attempt 2 of 3"}` |

**Code Location:**
```java
// DoOperationHandler.java - retryDoOperation()
auditService.recordRetryAttempt(flowId, orchName, stepName, retryCount, maxRetries, "DO");
```

---

### Phase: Retry Exhausted
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **RETRY_EXHAUSTED** | When max retries reached and step still fails | `DoOperationHandler.handleRetryExhausted()` | `{"retryCount": 3, "maxRetries": 3, "message": "Retry exhausted"}` |

**Code Location:**
```java
// DoOperationHandler.java - handleRetryExhausted()
auditService.recordStepFailure(flowId, orchName, stepName, workerService, 
    "Retry exhausted after " + maxRetries + " attempts", retryCount, "DO");
```

---

## 4️⃣ Rollback/UNDO Events

### Phase: Rollback Triggered
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **ROLLBACK_TRIGGERED** | When failure triggers rollback | `DoOperationHandler.handleRetryExhausted()` | `{"message": "Rollback triggered for orchestration", "triggerStep": "createClient"}` |

**Code Location:**
```java
// DoOperationHandler.java - handleRetryExhausted()
auditService.recordRollbackTriggered(flowId, orchName, stepName, reason);
```

---

### Phase: UNDO Start
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **UNDO_STARTED** | When UNDO message is sent to worker | `UndoOperationHandler.sendUndoMessage()` | `{"worker": "realm-service", "message": "UNDO operation started"}` |

**Code Location:**
```java
// UndoOperationHandler.java - sendUndoMessage()
auditService.recordUndoStart(flowId, orchName, stepName, workerService);
```

---

### Phase: UNDO Success
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **UNDO_COMPLETED** | When UNDO operation completes successfully | `UndoOperationHandler.handleUndoSuccess()` | `{"worker": "realm-service", "durationMs": 2000, "message": "UNDO operation completed"}` |

**Code Location:**
```java
// UndoOperationHandler.java - handleUndoSuccess()
auditService.recordUndoComplete(flowId, orchName, stepName, workerService, durationMs);
```

---

### Phase: UNDO Failure
| Event Name | When to Trigger | Recorded By | Example Details |
|------------|-----------------|-------------|-----------------|
| **UNDO_FAILED** | When UNDO operation fails | `UndoOperationHandler.handleUndoFailure()` | `{"worker": "realm-service", "message": "UNDO operation failed"}` |

**Code Location:**
```java
// UndoOperationHandler.java - handleUndoFailure()
auditService.recordUndoFailure(flowId, orchName, stepName, workerService, errorMessage);
```

---

## 📊 Complete Event Flow Examples

### Scenario 1: Successful Execution

```
1. ORCHESTRATION_STARTED      (Orchestration begins)
2. STEP_STARTED               (Step 1: createRealm - DO)
3. STEP_COMPLETED             (Step 1: createRealm - SUCCESS)
4. STEP_STARTED               (Step 2: createClient - DO)
5. STEP_COMPLETED             (Step 2: createClient - SUCCESS)
6. ORCHESTRATION_COMPLETED    (All steps successful)
```

---

### Scenario 2: Failure with Retry and Rollback

```
1. ORCHESTRATION_STARTED         (Orchestration begins)
2. STEP_STARTED                  (Step 1: createRealm - DO)
3. STEP_COMPLETED                (Step 1: createRealm - SUCCESS)
4. STEP_STARTED                  (Step 2: createClient - DO)
5. STEP_FAILED                   (Step 2: createClient - FAILED)
6. RETRY_ATTEMPT                 (Retry 1 of 3)
7. STEP_FAILED                   (Step 2: createClient - FAILED again)
8. RETRY_ATTEMPT                 (Retry 2 of 3)
9. STEP_FAILED                   (Step 2: createClient - FAILED again)
10. RETRY_ATTEMPT                (Retry 3 of 3)
11. STEP_FAILED                  (Step 2: createClient - FAILED again)
12. ORCHESTRATION_FAILED         (Max retries exhausted)
13. ROLLBACK_TRIGGERED           (Failure triggers rollback)
14. ROLLBACK_STARTED             (Rollback process begins)
15. UNDO_STARTED                 (Step 1: createRealm - UNDO)
16. UNDO_COMPLETED               (Step 1: createRealm - ROLLED BACK)
17. ROLLBACK_COMPLETED           (All steps rolled back)
```

---

### Scenario 3: Immediate Failure (FAIL_STEP)

```
1. ORCHESTRATION_STARTED         (Orchestration begins)
2. STEP_STARTED                  (Step 1: createRealm - DO)
3. STEP_COMPLETED                (Step 1: createRealm - SUCCESS)
4. STEP_STARTED                  (Step 2: createClient - DO)
5. STEP_FAILED                   (Step 2: createClient - Explicit FAIL_STEP)
6. ORCHESTRATION_FAILED          (Immediate failure)
7. ROLLBACK_TRIGGERED            (Failure triggers rollback)
8. ROLLBACK_STARTED              (Rollback process begins)
9. UNDO_STARTED                  (Step 1: createRealm - UNDO)
10. UNDO_COMPLETED               (Step 1: createRealm - ROLLED BACK)
11. ROLLBACK_COMPLETED           (All steps rolled back)
```

---

## 🎯 Event Trigger Matrix

| Event | Trigger Point | Service | Method |
|-------|--------------|---------|--------|
| ORCHESTRATION_STARTED | On start | OrchestrationExecutorService | startOrchestration() |
| ORCHESTRATION_COMPLETED | All steps done | DoOperationHandler | completeOrchestration() |
| ORCHESTRATION_FAILED | Retry exhausted | DoOperationHandler | handleRetryExhausted() |
| ROLLBACK_TRIGGERED | Failure occurs | DoOperationHandler | handleRetryExhausted() |
| ROLLBACK_STARTED | Undo begins | UndoOperationHandler | undoOrchestration() |
| ROLLBACK_COMPLETED | Undo done | UndoOperationHandler | completeUndoProcess() |
| STEP_STARTED | Message sent | DoOperationHandler | sendDoMessage() |
| STEP_COMPLETED | Success response | DoOperationHandler | handleDoSuccess() |
| STEP_FAILED | Failure response | DoOperationHandler | handleDoFailure() |
| RETRY_ATTEMPT | Before retry | DoOperationHandler | retryDoOperation() |
| UNDO_STARTED | UNDO message sent | UndoOperationHandler | sendUndoMessage() |
| UNDO_COMPLETED | UNDO success | UndoOperationHandler | handleUndoSuccess() |
| UNDO_FAILED | UNDO failure | UndoOperationHandler | handleUndoFailure() |

---

## 📝 Key Differences

### ROLLBACK_TRIGGERED vs ROLLBACK_STARTED

- **ROLLBACK_TRIGGERED**: Recorded when a failure is detected and the decision to rollback is made
  - Includes which step caused the trigger
  - Records the reason for rollback
  
- **ROLLBACK_STARTED**: Recorded when the actual rollback process begins
  - Includes count of steps that will be rolled back
  - Marks the beginning of UNDO operations

### ORCHESTRATION_FAILED vs ORCHESTRATION_COMPLETED

- **ORCHESTRATION_FAILED**: Orchestration could not complete successfully
  - May or may not have rollback
  
- **ORCHESTRATION_COMPLETED**: All steps completed successfully
  - Includes final status (SUCCESS)
  - Includes total duration

---

## 🔍 Querying Events

### Get All Orchestration-Level Events
```sql
SELECT * FROM audit_event
WHERE execution_id = 'abc123'
AND entity_type = 'ORCHESTRATION'
ORDER BY timestamp ASC;
```

### Get Rollback Timeline
```sql
SELECT * FROM audit_event
WHERE execution_id = 'abc123'
AND event_type IN ('ROLLBACK_TRIGGERED', 'ROLLBACK_STARTED', 'ROLLBACK_COMPLETED')
ORDER BY timestamp ASC;
```

### Get Complete Flow with Event Sequence
```sql
SELECT 
    event_type,
    step_name,
    status,
    timestamp,
    details
FROM v_execution_timeline
WHERE execution_id = 'abc123'
ORDER BY event_sequence;
```

---

**All orchestration lifecycle events are now fully tracked!** 🎉

