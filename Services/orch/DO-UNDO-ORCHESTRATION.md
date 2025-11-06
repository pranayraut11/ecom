# DO/UNDO Orchestration System - Technical Documentation

## Overview

This document describes the new DO/UNDO orchestration system that provides robust transaction-like capabilities for distributed orchestrations with automatic retry and rollback mechanisms.

## Architecture

### Key Components

1. **DoOperationHandler** - Handles DO (forward) operations
2. **UndoOperationHandler** - Handles UNDO (rollback) operations
3. **Dual Topic System** - Each step has separate DO and UNDO topics
4. **Retry Mechanism** - Configurable retry attempts for both DO and UNDO operations
5. **State Management** - Granular status tracking (DO_SUCCESS, DO_FAIL, UNDO_SUCCESS, UNDO_FAIL)

### Execution Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    Orchestration Start                          │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
            ┌─────────────────────┐
            │   DO Operations     │
            │   (Forward Flow)    │
            └─────────┬───────────┘
                      │
         ┌────────────┴────────────┐
         │                         │
    ┌────▼─────┐            ┌─────▼────┐
    │ SUCCESS  │            │  FAILURE │
    └────┬─────┘            └─────┬────┘
         │                        │
         ▼                        ▼
  ┌──────────────┐       ┌──────────────┐
  │ Next Step DO │       │ Retry DO?    │
  └──────┬───────┘       └──────┬───────┘
         │                      │
         │              ┌───────┴────────┐
         │              │                │
         │          ┌───▼───┐      ┌────▼─────┐
         │          │  YES  │      │    NO    │
         │          └───┬───┘      └────┬─────┘
         │              │               │
         │              └───────┐       │
         │                      │       ▼
         │                      │  ┌────────────┐
         │                      │  │ Trigger    │
         │                      │  │ UNDO       │
         │                      │  └────┬───────┘
         │                      │       │
         ▼                      ▼       ▼
  ┌──────────────────────────────────────┐
  │       All Steps Completed            │
  └──────────────────────────────────────┘
```

## Topic Structure

### For Each Step

- **DO Topic**: `orchestrator.{orchName}.{stepName}.do`
- **UNDO Topic**: `orchestrator.{orchName}.{stepName}.undo`
- **Legacy Topic**: `orchestrator.{orchName}.{stepName}` (for backward compatibility)

Example:
```
orchestrator.tenantCreation.createRealm.do
orchestrator.tenantCreation.createRealm.undo
```

## Status Enums

### New Status Values

| Status | Description |
|--------|-------------|
| `DO_SUCCESS` | Step DO operation completed successfully |
| `DO_FAIL` | Step DO operation failed (will retry if attempts remain) |
| `UNDO_SUCCESS` | Step UNDO operation completed successfully |
| `UNDO_FAIL` | Step UNDO operation failed (will retry if attempts remain) |
| `RETRY_EXHAUSTED` | All retry attempts exhausted for this step |

### Legacy Status Values (Still Supported)

| Status | Description |
|--------|-------------|
| `PENDING` | Step waiting to be executed |
| `IN_PROGRESS` | Step currently executing |
| `COMPLETED` | Step completed (legacy) |
| `FAILED` | Step failed (legacy) |
| `UNDOING` | Step currently being undone |
| `UNDONE` | Orchestration fully undone |

## Retry Mechanism

### Configuration

- **Default Max Retries**: 3 attempts per step
- **Configurable**: Set `maxRetries` in `OrchestrationStepTemplate`
- **Retry Count**: Tracked per step in `OrchestrationStepRun`

### Retry Logic

1. **DO Operation Retry**:
   - On failure, check if `retryCount < maxRetries`
   - If yes, increment retry count and resend to DO topic
   - If no, mark as `RETRY_EXHAUSTED` and trigger UNDO

2. **UNDO Operation Retry**:
   - On failure, check if `retryCount < maxRetries`
   - If yes, increment retry count and resend to UNDO topic
   - If no, mark as `UNDO_FAIL` and stop orchestration

## Orchestration Types

### Sequential Orchestration

**DO Flow**:
```
Step1(DO) → Success → Step2(DO) → Success → Step3(DO) → Success → COMPLETED
                ↓ Fail             ↓ Fail             ↓ Fail
              Retry                Retry              Retry
                ↓ Exhausted        ↓ Exhausted        ↓ Exhausted
              UNDO Flow          UNDO Flow          UNDO Flow
```

**UNDO Flow** (Reverse Order):
```
Step3(UNDO) → Success → Step2(UNDO) → Success → Step1(UNDO) → Success → UNDONE
```

### Parallel Orchestration

**DO Flow**:
```
       ┌─────────┐
       │ START   │
       └────┬────┘
            │
    ┌───────┼───────┐
    │       │       │
┌───▼──┐ ┌──▼──┐ ┌─▼───┐
│Step1 │ │Step2│ │Step3│
│(DO)  │ │(DO) │ │(DO) │
└───┬──┘ └──┬──┘ └─┬───┘
    │       │      │
    └───────┼──────┘
            │
      All Success?
            │
         ┌──▼──┐
         │DONE │
         └─────┘
```

**UNDO Flow** (All in Parallel):
```
       ┌──────────┐
       │ TRIGGER  │
       │  UNDO    │
       └────┬─────┘
            │
    ┌───────┼────────┐
    │       │        │
┌───▼───┐ ┌─▼────┐ ┌▼────┐
│Step1  │ │Step2 │ │Step3│
│(UNDO) │ │(UNDO)│ │(UNDO│
└───┬───┘ └──┬───┘ └─┬───┘
    │        │       │
    └────────┼───────┘
             │
       All Success?
             │
          ┌──▼───┐
          │UNDONE│
          └──────┘
```

## Message Format

### DO Message Headers

```json
{
  "flowId": "uuid-123",
  "stepName": "createRealm",
  "action": "DO",
  "seq": 1,
  "orchestrationName": "tenantCreation"
}
```

### UNDO Message Headers

```json
{
  "flowId": "uuid-123",
  "stepName": "createRealm",
  "action": "UNDO",
  "seq": 1,
  "orchestrationName": "tenantCreation"
}
```

### Response Message Headers

```json
{
  "flowId": "uuid-123",
  "stepName": "createRealm",
  "action": "DO" | "UNDO",
  "status": true | false,
  "errorMessage": "Error details if status is false"
}
```

## Database Schema

### orchestration_step_template

```sql
CREATE TABLE orchestration_step_template (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    seq INTEGER NOT NULL,
    step_name VARCHAR(255) NOT NULL,
    object_type VARCHAR(255) NOT NULL,
    topic_name VARCHAR(255) NOT NULL,      -- Legacy
    do_topic VARCHAR(255) NOT NULL,         -- NEW
    undo_topic VARCHAR(255) NOT NULL,       -- NEW
    max_retries INTEGER DEFAULT 3 NOT NULL, -- NEW
    created_at TIMESTAMP NOT NULL
);
```

### orchestration_step_run

```sql
CREATE TABLE orchestration_step_run (
    id BIGSERIAL PRIMARY KEY,
    orchestration_run_id BIGINT NOT NULL,
    step_name VARCHAR(255) NOT NULL,
    seq INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    worker_service VARCHAR(255),
    error_message TEXT,
    retry_count INTEGER DEFAULT 0 NOT NULL,  -- NEW
    max_retries INTEGER DEFAULT 3 NOT NULL,  -- NEW
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    undone_at TIMESTAMP
);
```

## Worker Implementation Guide

### Worker Service Requirements

Your worker service must:

1. **Listen to Both Topics**:
   - DO topic: `orchestrator.{orchName}.{stepName}.do`
   - UNDO topic: `orchestrator.{orchName}.{stepName}.undo`

2. **Handle DO Operation**:
   ```java
   @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.do")
   public void handleDo(ExecutionMessage message) {
       try {
           // Perform forward operation
           createRealm(message.getPayload());
           
           // Send success response
           sendResponse(message, "DO", true, null);
       } catch (Exception e) {
           // Send failure response
           sendResponse(message, "DO", false, e.getMessage());
       }
   }
   ```

3. **Handle UNDO Operation**:
   ```java
   @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.undo")
   public void handleUndo(ExecutionMessage message) {
       try {
           // Perform rollback operation
           deleteRealm(message.getPayload());
           
           // Send success response
           sendResponse(message, "UNDO", true, null);
       } catch (Exception e) {
           // Send failure response
           sendResponse(message, "UNDO", false, e.getMessage());
       }
   }
   ```

4. **Send Response**:
   ```java
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
   }
   ```

## Migration Guide

### Migrating from Old System

1. **Run Migration Script**:
   ```bash
   psql -U postgres -d orchestrator_db -f migration-do-undo-topics.sql
   ```

2. **Update Worker Services**:
   - Add UNDO topic listeners
   - Implement UNDO logic for each step
   - Update response messages to include `action` field

3. **Re-register Orchestrations**:
   - Send new registration messages
   - System will create DO and UNDO topics automatically

4. **Backward Compatibility**:
   - Legacy topic still exists
   - Old workers continue to work
   - Gradually migrate to DO/UNDO pattern

## Monitoring and Debugging

### Key Metrics to Monitor

1. **Retry Rate**: How often steps are retried
2. **UNDO Trigger Rate**: How often rollbacks occur
3. **UNDO Success Rate**: Success rate of rollback operations
4. **Retry Exhaustion Rate**: How often retries are exhausted

### Debug Logs

Enable DEBUG logging for detailed flow tracking:

```yaml
logging:
  level:
    com.ecom.orchestrator.service.DoOperationHandler: DEBUG
    com.ecom.orchestrator.service.UndoOperationHandler: DEBUG
```

### Query Step Status

```sql
-- Check step execution status
SELECT 
    or_run.flow_id,
    or_run.orch_name,
    or_run.status as orchestration_status,
    sr.step_name,
    sr.seq,
    sr.status as step_status,
    sr.retry_count,
    sr.max_retries,
    sr.error_message
FROM orchestration_run or_run
JOIN orchestration_step_run sr ON or_run.id = sr.orchestration_run_id
WHERE or_run.flow_id = 'your-flow-id'
ORDER BY sr.seq;
```

## Best Practices

1. **Idempotency**: Ensure DO and UNDO operations are idempotent
2. **State Management**: Store enough state to properly undo operations
3. **Error Handling**: Provide detailed error messages for debugging
4. **Timeouts**: Set appropriate timeouts for long-running operations
5. **Monitoring**: Track retry rates and UNDO triggers
6. **Testing**: Test both happy path and failure scenarios thoroughly

## Troubleshooting

### Issue: Step stuck in IN_PROGRESS

**Cause**: Worker didn't send response or response was lost

**Solution**: 
- Check worker logs
- Verify topic configuration
- Check for network issues
- Implement timeout mechanism

### Issue: UNDO keeps failing

**Cause**: UNDO operation not properly implemented or resource already deleted

**Solution**:
- Make UNDO operations idempotent
- Check if resource exists before attempting deletion
- Handle "already deleted" as success case

### Issue: Retry exhausted too quickly

**Cause**: maxRetries set too low

**Solution**:
- Increase maxRetries in step template
- Re-register orchestration with higher retry count

## API Examples

### Start Orchestration

```bash
POST /api/orchestrations/execute
Content-Type: application/json

{
  "orchestrationName": "tenantCreation",
  "payload": {
    "tenantName": "acme-corp",
    "domain": "acme.example.com"
  }
}
```

### Check Execution Status

```bash
GET /api/orchestrations/history?orchName=tenantCreation&status=IN_PROGRESS
```

### View Execution Details

```bash
GET /api/orchestrations/details/{flowId}
```

## Summary

The DO/UNDO orchestration system provides:

✅ **Robust Error Handling** - Automatic retries with configurable limits
✅ **Automatic Rollback** - UNDO operations triggered on failure
✅ **State Tracking** - Granular status for each step
✅ **Sequential & Parallel** - Support for both execution patterns
✅ **Backward Compatible** - Works with existing workers
✅ **Observable** - Comprehensive logging and status tracking

This system ensures distributed transactions can be reliably executed and rolled back, providing transaction-like guarantees in a distributed microservices environment.

