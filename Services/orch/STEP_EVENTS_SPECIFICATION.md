# 📊 Step-Level Audit Events - Complete Specification

## Overview

This document provides the complete specification for all step-level audit events with exact details captured.

---

## Step-Level Event Specification

| Phase | Event Name | When to Trigger | Captured Details | Code Location |
|-------|-----------|-----------------|------------------|---------------|
| **Step Start** | `STEP_STARTED` | Before sending message to worker | • Worker service name<br>• Operation type (DO/UNDO)<br>• Message: "Step execution started" | `DoOperationHandler.sendDoMessage()`<br>`UndoOperationHandler.sendUndoMessage()` |
| **Step Success** | `STEP_SUCCESS` | On receiving successful acknowledgment from worker | • Worker service name<br>• Duration in milliseconds<br>• Retry count<br>• Operation type (DO/UNDO)<br>• Status: "SUCCESS" | `DoOperationHandler.handleDoSuccess()`<br>`UndoOperationHandler.handleUndoSuccess()` |
| **Step Failure** | `STEP_FAILED` | On receiving failure response or timeout | • Worker service name<br>• Error message<br>• Failure reason<br>• Retry count<br>• Operation type (DO/UNDO)<br>• Status: "FAILED" | `DoOperationHandler.handleDoFailure()`<br>`UndoOperationHandler.handleUndoFailure()` |
| **Step Retry** | `STEP_RETRY_TRIGGERED` | Each time retry logic triggers | • Retry attempt count<br>• Max retries allowed<br>• Backoff time in milliseconds<br>• Operation type (DO/UNDO)<br>• Message: "Retry attempt X of Y" | `DoOperationHandler.retryDoOperation()` |

---

## Detailed Event Specifications

### 1. STEP_STARTED

**Purpose:** Track when a step execution begins (message sent to worker)

**Event Details:**
```json
{
  "eventType": "STEP_STARTED",
  "entityType": "STEP",
  "stepName": "createRealm",
  "serviceName": "realm-worker-service",
  "operationType": "DO",
  "timestamp": "2025-11-04T10:00:00.123Z",
  "details": {
    "worker": "realm-worker-service",
    "message": "Step execution started"
  }
}
```

**Implementation:**
```java
// DoOperationHandler.sendDoMessage()
auditService.recordStepStart(flowId, orchName, stepName, workerService, "DO");
```

---

### 2. STEP_SUCCESS

**Purpose:** Track successful step completion with performance metrics

**Event Details:**
```json
{
  "eventType": "STEP_SUCCESS",
  "entityType": "STEP",
  "stepName": "createRealm",
  "status": "SUCCESS",
  "serviceName": "realm-worker-service",
  "operationType": "DO",
  "durationMs": 5234,
  "retryCount": 0,
  "timestamp": "2025-11-04T10:00:05.357Z",
  "details": {
    "worker": "realm-worker-service",
    "durationMs": 5234,
    "retryCount": 0,
    "message": "Step completed successfully"
  }
}
```

**Key Captured Metrics:**
- ✅ **Duration** - How long the step took (ms)
- ✅ **Retry Count** - How many retries occurred (0 if first attempt succeeded)
- ✅ **Worker Service** - Which service executed the step

**Implementation:**
```java
// DoOperationHandler.handleDoSuccess()
Long durationMs = Duration.between(stepRun.getStartedAt(), LocalDateTime.now()).toMillis();
auditService.recordStepSuccess(
    flowId, 
    orchName, 
    stepName, 
    workerService, 
    durationMs, 
    "DO",
    stepRun.getRetryCount()
);
```

---

### 3. STEP_FAILED

**Purpose:** Track step failures with detailed error information

**Event Details:**
```json
{
  "eventType": "STEP_FAILED",
  "entityType": "STEP",
  "stepName": "createClient",
  "status": "FAILED",
  "serviceName": "client-worker-service",
  "operationType": "DO",
  "retryCount": 2,
  "reason": "Connection timeout to Keycloak service",
  "timestamp": "2025-11-04T10:00:15.789Z",
  "details": {
    "worker": "client-worker-service",
    "retryCount": 2,
    "message": "Step execution failed"
  }
}
```

**Key Captured Information:**
- ✅ **Error Message** - Technical error from worker
- ✅ **Failure Reason** - Business-friendly reason
- ✅ **Retry Count** - How many attempts have been made
- ✅ **Worker Service** - Which service failed

**Implementation:**
```java
// DoOperationHandler.handleDoFailure()
auditService.recordStepFailure(
    flowId,
    orchName,
    stepName,
    workerService,
    errorMessage,
    stepRun.getRetryCount(),
    "DO"
);
```

---

### 4. STEP_RETRY_TRIGGERED

**Purpose:** Track each retry attempt with timing information

**Event Details:**
```json
{
  "eventType": "STEP_RETRY_TRIGGERED",
  "entityType": "STEP",
  "stepName": "createClient",
  "serviceName": null,
  "operationType": "DO",
  "retryCount": 2,
  "timestamp": "2025-11-04T10:00:20.123Z",
  "details": {
    "retryCount": 2,
    "maxRetries": 3,
    "backoffMs": 5000,
    "message": "Retry attempt 2 of 3"
  }
}
```

**Key Captured Information:**
- ✅ **Retry Attempt Count** - Current retry number (1, 2, 3...)
- ✅ **Max Retries** - Maximum allowed retries
- ✅ **Backoff Time** - Delay before retry in milliseconds
- ✅ **Operation Type** - DO or UNDO

**Implementation:**
```java
// DoOperationHandler.retryDoOperation()
auditService.recordRetryAttempt(
    flowId,
    orchName,
    stepName,
    stepRun.getRetryCount(),
    stepRun.getMaxRetries(),
    "DO",
    5000L  // Backoff time in ms
);
```

---

## Event Flow Examples

### Scenario 1: First Attempt Success
```json
[
  {
    "eventType": "STEP_STARTED",
    "stepName": "createRealm",
    "timestamp": "2025-11-04T10:00:00.000Z"
  },
  {
    "eventType": "STEP_SUCCESS",
    "stepName": "createRealm",
    "durationMs": 5234,
    "retryCount": 0,
    "timestamp": "2025-11-04T10:00:05.234Z"
  }
]
```

**Timeline:** START → SUCCESS (5.2s, no retries)

---

### Scenario 2: Success After Retries
```json
[
  {
    "eventType": "STEP_STARTED",
    "stepName": "createClient",
    "timestamp": "2025-11-04T10:00:00.000Z"
  },
  {
    "eventType": "STEP_FAILED",
    "stepName": "createClient",
    "retryCount": 0,
    "reason": "Connection timeout",
    "timestamp": "2025-11-04T10:00:10.000Z"
  },
  {
    "eventType": "STEP_RETRY_TRIGGERED",
    "stepName": "createClient",
    "retryCount": 1,
    "details": {"backoffMs": 5000},
    "timestamp": "2025-11-04T10:00:10.123Z"
  },
  {
    "eventType": "STEP_FAILED",
    "stepName": "createClient",
    "retryCount": 1,
    "reason": "Connection timeout",
    "timestamp": "2025-11-04T10:00:20.000Z"
  },
  {
    "eventType": "STEP_RETRY_TRIGGERED",
    "stepName": "createClient",
    "retryCount": 2,
    "details": {"backoffMs": 5000},
    "timestamp": "2025-11-04T10:00:20.123Z"
  },
  {
    "eventType": "STEP_SUCCESS",
    "stepName": "createClient",
    "durationMs": 3456,
    "retryCount": 2,
    "timestamp": "2025-11-04T10:00:28.456Z"
  }
]
```

**Timeline:** START → FAIL → RETRY → FAIL → RETRY → SUCCESS (28.5s, 2 retries)

---

### Scenario 3: Retry Exhausted
```json
[
  {
    "eventType": "STEP_STARTED",
    "stepName": "createClient",
    "timestamp": "2025-11-04T10:00:00.000Z"
  },
  {
    "eventType": "STEP_FAILED",
    "retryCount": 0,
    "timestamp": "2025-11-04T10:00:05.000Z"
  },
  {
    "eventType": "STEP_RETRY_TRIGGERED",
    "retryCount": 1,
    "timestamp": "2025-11-04T10:00:05.123Z"
  },
  {
    "eventType": "STEP_FAILED",
    "retryCount": 1,
    "timestamp": "2025-11-04T10:00:10.000Z"
  },
  {
    "eventType": "STEP_RETRY_TRIGGERED",
    "retryCount": 2,
    "timestamp": "2025-11-04T10:00:10.123Z"
  },
  {
    "eventType": "STEP_FAILED",
    "retryCount": 2,
    "timestamp": "2025-11-04T10:00:15.000Z"
  },
  {
    "eventType": "STEP_RETRY_TRIGGERED",
    "retryCount": 3,
    "timestamp": "2025-11-04T10:00:15.123Z"
  },
  {
    "eventType": "STEP_FAILED",
    "retryCount": 3,
    "reason": "Retry exhausted after 3 attempts",
    "timestamp": "2025-11-04T10:00:20.000Z"
  },
  {
    "eventType": "ORCHESTRATION_FAILED",
    "timestamp": "2025-11-04T10:00:20.456Z"
  },
  {
    "eventType": "ROLLBACK_TRIGGERED",
    "timestamp": "2025-11-04T10:00:20.789Z"
  }
]
```

**Timeline:** START → FAIL → RETRY(1) → FAIL → RETRY(2) → FAIL → RETRY(3) → FAIL → ORCHESTRATION FAILED → ROLLBACK

---

## Database Queries

### Get All Events for a Step
```sql
SELECT 
    event_type,
    status,
    retry_count,
    duration_ms,
    timestamp,
    reason,
    details
FROM audit_event
WHERE execution_id = 'abc123'
AND step_name = 'createClient'
ORDER BY timestamp ASC;
```

### Analyze Retry Patterns
```sql
SELECT 
    step_name,
    COUNT(*) as retry_count,
    AVG((details->>'backoffMs')::int) as avg_backoff_ms
FROM audit_event
WHERE execution_id = 'abc123'
AND event_type = 'STEP_RETRY_TRIGGERED'
GROUP BY step_name;
```

### Get Success Metrics
```sql
SELECT 
    step_name,
    duration_ms,
    retry_count,
    service_name,
    timestamp
FROM audit_event
WHERE execution_id = 'abc123'
AND event_type = 'STEP_SUCCESS'
ORDER BY duration_ms DESC;
```

---

## Performance Analysis Queries

### Average Step Duration by Name
```sql
SELECT 
    step_name,
    AVG(duration_ms) as avg_duration_ms,
    MIN(duration_ms) as min_duration_ms,
    MAX(duration_ms) as max_duration_ms,
    COUNT(*) as execution_count
FROM audit_event
WHERE event_type = 'STEP_SUCCESS'
AND timestamp >= NOW() - INTERVAL '7 days'
GROUP BY step_name
ORDER BY avg_duration_ms DESC;
```

### Retry Success Rate
```sql
SELECT 
    step_name,
    COUNT(CASE WHEN event_type = 'STEP_RETRY_TRIGGERED' THEN 1 END) as total_retries,
    COUNT(CASE WHEN event_type = 'STEP_SUCCESS' AND retry_count > 0 THEN 1 END) as successful_after_retry,
    ROUND(
        COUNT(CASE WHEN event_type = 'STEP_SUCCESS' AND retry_count > 0 THEN 1 END)::numeric / 
        NULLIF(COUNT(CASE WHEN event_type = 'STEP_RETRY_TRIGGERED' THEN 1 END), 0) * 100,
        2
    ) as retry_success_rate_pct
FROM audit_event
WHERE timestamp >= NOW() - INTERVAL '7 days'
GROUP BY step_name;
```

---

## Summary Table

| Event | Trigger | Duration | Retry Count | Error Info | Backoff Time |
|-------|---------|----------|-------------|------------|--------------|
| STEP_STARTED | ✅ Before send | ❌ | ❌ | ❌ | ❌ |
| STEP_SUCCESS | ✅ On success | ✅ | ✅ | ❌ | ❌ |
| STEP_FAILED | ✅ On failure | ❌ | ✅ | ✅ | ❌ |
| STEP_RETRY_TRIGGERED | ✅ Each retry | ❌ | ✅ | ❌ | ✅ |

---

**All step-level events now capture comprehensive details for monitoring and analysis! 🎯**

