# handleFailResponse Retry Implementation

## Overview
Updated the `handleFailResponse` method in `UndoOperationHandler` to implement retry logic before triggering UNDO operations. This ensures that failed steps are retried according to their retry configuration before rolling back.

## Implementation Details

### Updated Flow in handleFailResponse

**Previous Behavior:**
- Step fails → Immediately mark as FAILED → Trigger UNDO

**New Behavior:**
- Step fails → Check retry count → Retry if available → Trigger UNDO only when retries exhausted

### Key Changes

#### 1. handleFailResponse Method
```
Location: UndoOperationHandler.java
```
- **Added**: Retry count check before triggering UNDO
- **Added**: Audit event recording for step failure
- **Updated**: Only triggers UNDO when retries are exhausted

**Logic Flow:**
1. Receive FAIL_STEP action from worker
2. Update step error message
3. Record audit event for step failure
4. Check if `stepRun.getRetryCount() < stepRun.getMaxRetries()`
   - **YES**: Call `retryFailedStep()`
   - **NO**: Call `handleFailRetryExhausted()`

#### 2. retryFailedStep Method (NEW)
```
private void retryFailedStep(OrchestrationRun, OrchestrationStepRun, ExecutionMessage)
```
**Purpose**: Retry the failed step by resending the DO message

**Actions:**
1. Increment retry count
2. Set status to IN_PROGRESS
3. Update lastRetryAt timestamp
4. Record audit event for retry attempt
5. Fetch step template to get DO topic
6. Send DO message for retry via `sendDoMessageForRetry()`

**Audit Events:**
- Records retry attempt with current/max retry counts
- Includes backoff time (default 5000ms)
- Operation type: "DO"

#### 3. handleFailRetryExhausted Method (NEW)
```
private void handleFailRetryExhausted(OrchestrationRun, OrchestrationStepRun, ExecutionMessage)
```
**Purpose**: Handle scenario when all retries are exhausted

**Actions:**
1. Mark step as FAILED
2. Set completedAt timestamp
3. Mark orchestration run as FAILED
4. Find all successfully completed steps (DO_SUCCESS)
5. Trigger UNDO for those steps
6. If no steps to undo, complete orchestration as failed

**This is the original logic from handleFailResponse - moved here**

#### 4. sendDoMessageForRetry Method (NEW)
```
private void sendDoMessageForRetry(String flowId, OrchestrationStepTemplate, ExecutionMessage)
```
**Purpose**: Send DO message to worker for retry attempt

**Actions:**
1. Update message headers with step information
   - flowId
   - stepName
   - action: "DO"
   - seq: step sequence number
2. Send message to DO topic
3. Handle exception: If sending fails, directly trigger UNDO

**Error Handling:**
- Catches any exception during message sending
- Falls back to `handleFailRetryExhausted()` if retry message cannot be sent

## Retry Flow Diagram

```
Worker sends FAIL_STEP
        ↓
handleFailResponse()
        ↓
    Record Failure
        ↓
    Check Retry Count
        ↓
  ┌─────┴─────┐
  ↓           ↓
Has Retries   No Retries
  ↓           ↓
retryFailedStep()   handleFailRetryExhausted()
  ↓                 ↓
Increment Count     Mark as FAILED
  ↓                 ↓
Set IN_PROGRESS     Update Orchestration
  ↓                 ↓
Record Retry        Find DO_SUCCESS steps
  ↓                 ↓
Get DO Topic        Trigger UNDO
  ↓
sendDoMessageForRetry()
  ↓
Send to DO Topic
  ↓
Worker retries
```

## Consistency with DO Operation Retry

This implementation follows the same pattern as `DoOperationHandler`:
- Similar retry check: `retryCount < maxRetries`
- Similar retry logic: increment count, update status, record audit
- Similar message sending: fetch template, send to DO topic
- Similar exhaustion handling: mark as FAILED, trigger UNDO

## Audit Events Generated

### On Step Failure
```json
{
  "eventType": "STEP_FAILURE",
  "flowId": "...",
  "orchName": "...",
  "stepName": "...",
  "workerService": "...",
  "errorMessage": "...",
  "retryCount": 0,
  "operationType": "DO"
}
```

### On Retry Attempt
```json
{
  "eventType": "RETRY_ATTEMPT",
  "flowId": "...",
  "orchName": "...",
  "stepName": "...",
  "retryCount": 1,
  "maxRetries": 3,
  "operationType": "DO",
  "backoffTime": 5000
}
```

## Configuration

Retry behavior is controlled by:
- `stepRun.maxRetries`: Maximum retry attempts (configured in step template)
- `stepRun.retryCount`: Current retry count (tracked per step run)
- Backoff time: Default 5000ms (can be made configurable)

## Benefits

1. **Resilience**: Transient failures don't immediately trigger rollback
2. **Cost Optimization**: Avoids expensive UNDO operations for temporary issues
3. **Consistency**: Matches DO operation retry behavior
4. **Auditability**: Full tracking of retry attempts
5. **Error Handling**: Graceful fallback if retry messaging fails

## Testing Scenarios

### Scenario 1: Retry Succeeds
1. Step fails (retryCount=0, maxRetries=3)
2. System retries → Worker processes successfully
3. No UNDO triggered

### Scenario 2: Retries Exhausted
1. Step fails (retryCount=0, maxRetries=3)
2. Retry 1 fails (retryCount=1)
3. Retry 2 fails (retryCount=2)
4. Retry 3 fails (retryCount=3)
5. retryCount (3) == maxRetries (3)
6. Trigger UNDO for all DO_SUCCESS steps

### Scenario 3: No Retries Configured
1. Step fails (retryCount=0, maxRetries=0)
2. Immediately trigger UNDO (retries exhausted)

### Scenario 4: Retry Message Send Fails
1. Step fails, retry available
2. Attempt to send DO message fails
3. Exception caught
4. Directly call handleFailRetryExhausted()
5. Trigger UNDO

## Migration Notes

- **No database changes required**: Uses existing retry fields
- **Backward compatible**: Works with existing step templates
- **No API changes**: Internal implementation only
- **Audit events**: New events added but existing structure maintained

## Related Files

- `UndoOperationHandler.java`: Main implementation
- `DoOperationHandler.java`: Reference implementation for retry pattern
- `AuditService.java`: Audit event recording
- `OrchestrationStepRun.java`: Retry count tracking

## Date
November 13, 2025

