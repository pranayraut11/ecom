# Integrated Audit Feature - Implementation Guide

## 🎯 Overview

The Audit Feature provides a comprehensive execution timeline for every orchestration flow, capturing all orchestration and step-level events including DO operations, UNDO/rollback flows, retries, and failures.

---

## 📋 Features Implemented

### ✅ Complete Event Tracking
- Orchestration lifecycle events (started, completed, failed)
- Step execution events (started, success, failed)
- Retry attempts with counts
- Rollback/UNDO operations
- Detailed failure reasons and context

### ✅ Asynchronous Recording
- Events recorded asynchronously using `@Async`
- Non-blocking orchestration flow
- Separate transactions for audit events

### ✅ Rich Metadata
- JSONB details field for flexible metadata
- Duration tracking for performance analysis
- Worker service attribution
- Operation type (DO/UNDO) tracking

### ✅ REST API
- Get complete timeline for an execution
- Filter by event type, status, date range
- Paginated responses (ready for enhancement)

---

## 🏗️ Architecture

### Components

```
┌─────────────────────────────────────────┐
│     Orchestration Engine                │
│  (DoOperationHandler,                   │
│   UndoOperationHandler,                 │
│   OrchestrationExecutorService)         │
└────────────┬────────────────────────────┘
             │
             │ Records events
             ↓
┌─────────────────────────────────────────┐
│         AuditService                    │
│  - recordEvent() @Async                 │
│  - recordStepStart()                    │
│  - recordStepSuccess()                  │
│  - recordStepFailure()                  │
│  - recordRetryAttempt()                 │
│  - recordUndoStart()                    │
│  - recordUndoComplete()                 │
│  - recordRollbackTriggered()            │
└────────────┬────────────────────────────┘
             │
             │ Persists asynchronously
             ↓
┌─────────────────────────────────────────┐
│      AuditEventRepository               │
│         (JPA Repository)                │
└────────────┬────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────┐
│      audit_event table                  │
│      (PostgreSQL + JSONB)               │
└─────────────────────────────────────────┘
```

---

## 📊 Database Schema

### audit_event Table

| Column | Type | Description |
|--------|------|-------------|
| `id` | VARCHAR(36) | UUID primary key |
| `execution_id` | VARCHAR(255) | Foreign key to orchestration run (flowId) |
| `orch_name` | VARCHAR(255) | Orchestration name |
| `entity_type` | ENUM | ORCHESTRATION or STEP |
| `step_name` | VARCHAR(255) | Step name (nullable) |
| `event_type` | ENUM | Event type (see Event Types below) |
| `status` | VARCHAR(50) | Current status |
| `timestamp` | TIMESTAMP | Event occurrence time |
| `reason` | TEXT | Failure reason or context |
| `details` | JSONB | Additional metadata |
| `created_by` | VARCHAR(255) | Who triggered the event |
| `service_name` | VARCHAR(255) | Source service name |
| `operation_type` | VARCHAR(20) | DO or UNDO |
| `duration_ms` | BIGINT | Duration in milliseconds |
| `retry_count` | INTEGER | Retry attempt number |

### Event Types

#### Orchestration-Level
- `ORCHESTRATION_STARTED`
- `ORCHESTRATION_COMPLETED`
- `ORCHESTRATION_FAILED`

#### Step-Level (DO)
- `STEP_STARTED`
- `STEP_COMPLETED`
- `STEP_FAILED`

#### Retry
- `RETRY_ATTEMPT`
- `RETRY_EXHAUSTED`

#### Rollback/UNDO
- `ROLLBACK_TRIGGERED`
- `UNDO_STARTED`
- `UNDO_COMPLETED`
- `UNDO_FAILED`
- `ROLLBACK_COMPLETED`

---

## 🔌 Integration Points

### 1. Orchestration Start
**Location:** `OrchestrationExecutorService.startOrchestration()`

```java
auditService.recordOrchestrationStart(flowId, orchName, initiator);
```

### 2. Step Execution Start
**Location:** `DoOperationHandler.sendDoMessage()`

```java
auditService.recordStepStart(flowId, orchName, stepName, workerService, "DO");
```

### 3. Step Success
**Location:** `DoOperationHandler.handleDoSuccess()`

```java
auditService.recordStepSuccess(flowId, orchName, stepName, workerService, durationMs, "DO");
```

### 4. Step Failure
**Location:** `DoOperationHandler.handleDoFailure()`

```java
auditService.recordStepFailure(flowId, orchName, stepName, workerService, errorMessage, retryCount, "DO");
```

### 5. Retry Attempt
**Location:** `DoOperationHandler.retryDoOperation()`

```java
auditService.recordRetryAttempt(flowId, orchName, stepName, retryCount, maxRetries, "DO");
```

### 6. Rollback Triggered
**Location:** `DoOperationHandler.handleRetryExhausted()`

```java
auditService.recordRollbackTriggered(flowId, orchName, stepName, reason);
```

### 7. UNDO Start
**Location:** `UndoOperationHandler.sendUndoMessage()`

```java
auditService.recordUndoStart(flowId, orchName, stepName, workerService);
```

### 8. UNDO Success
**Location:** `UndoOperationHandler.handleUndoSuccess()`

```java
auditService.recordUndoComplete(flowId, orchName, stepName, workerService, durationMs);
```

### 9. UNDO Failure
**Location:** `UndoOperationHandler.handleUndoFailure()`

```java
auditService.recordUndoFailure(flowId, orchName, stepName, workerService, errorMessage);
```

---

## 🌐 API Endpoints

### Get Audit Timeline

```http
GET /api/audit/{executionId}
```

**Query Parameters:**
- `eventType` (optional) - Filter by event type (e.g., `STEP_FAILED`)
- `status` (optional) - Filter by status (e.g., `FAILED`)
- `from` (optional) - Start timestamp (ISO 8601)
- `to` (optional) - End timestamp (ISO 8601)

**Example Request:**
```bash
curl "http://localhost:8080/api/audit/abc123?eventType=STEP_FAILED&from=2025-11-03T20:00:00"
```

**Example Response:**
```json
{
  "executionId": "abc123",
  "orchName": "tenantCreation",
  "totalEvents": 15,
  "failedEvents": 2,
  "retryEvents": 3,
  "events": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "ORCHESTRATION",
      "stepName": null,
      "eventType": "ORCHESTRATION_STARTED",
      "status": null,
      "timestamp": "2025-11-03T20:10:24.123Z",
      "reason": null,
      "details": {
        "initiator": "tenant-management-service",
        "message": "Orchestration execution started"
      },
      "createdBy": "tenant-management-service",
      "serviceName": "tenant-management-service",
      "operationType": null,
      "durationMs": null,
      "retryCount": null
    },
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "STEP",
      "stepName": "createRealm",
      "eventType": "STEP_STARTED",
      "status": null,
      "timestamp": "2025-11-03T20:10:30.456Z",
      "reason": null,
      "details": {
        "worker": "realm-worker",
        "message": "Step execution started"
      },
      "createdBy": null,
      "serviceName": "realm-worker",
      "operationType": "DO",
      "durationMs": null,
      "retryCount": null
    },
    {
      "id": "770e8400-e29b-41d4-a716-446655440002",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "STEP",
      "stepName": "createRealm",
      "eventType": "STEP_COMPLETED",
      "status": "SUCCESS",
      "timestamp": "2025-11-03T20:10:35.789Z",
      "reason": null,
      "details": {
        "worker": "realm-worker",
        "durationMs": 5333,
        "message": "Step completed successfully"
      },
      "createdBy": null,
      "serviceName": "realm-worker",
      "operationType": "DO",
      "durationMs": 5333,
      "retryCount": null
    },
    {
      "id": "880e8400-e29b-41d4-a716-446655440003",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "STEP",
      "stepName": "createClient",
      "eventType": "STEP_FAILED",
      "status": "FAILED",
      "timestamp": "2025-11-03T20:10:45.123Z",
      "reason": "Timeout calling Keycloak",
      "details": {
        "worker": "client-worker",
        "retryCount": 0,
        "message": "Step execution failed"
      },
      "createdBy": null,
      "serviceName": "client-worker",
      "operationType": "DO",
      "durationMs": null,
      "retryCount": 0
    },
    {
      "id": "990e8400-e29b-41d4-a716-446655440004",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "STEP",
      "stepName": "createClient",
      "eventType": "RETRY_ATTEMPT",
      "status": null,
      "timestamp": "2025-11-03T20:10:46.456Z",
      "reason": null,
      "details": {
        "retryCount": 1,
        "maxRetries": 3,
        "message": "Retry attempt 1 of 3"
      },
      "createdBy": null,
      "serviceName": null,
      "operationType": "DO",
      "durationMs": null,
      "retryCount": 1
    },
    {
      "id": "aa0e8400-e29b-41d4-a716-446655440005",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "ORCHESTRATION",
      "stepName": "createClient",
      "eventType": "ROLLBACK_TRIGGERED",
      "status": null,
      "timestamp": "2025-11-03T20:10:50.789Z",
      "reason": "Rollback triggered due to retry exhaustion",
      "details": {
        "message": "Rollback triggered for orchestration",
        "triggerStep": "createClient"
      },
      "createdBy": null,
      "serviceName": null,
      "operationType": null,
      "durationMs": null,
      "retryCount": null
    },
    {
      "id": "bb0e8400-e29b-41d4-a716-446655440006",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "STEP",
      "stepName": "createRealm",
      "eventType": "UNDO_STARTED",
      "status": null,
      "timestamp": "2025-11-03T20:10:51.123Z",
      "reason": null,
      "details": {
        "worker": "realm-worker",
        "message": "UNDO operation started"
      },
      "createdBy": null,
      "serviceName": "realm-worker",
      "operationType": "UNDO",
      "durationMs": null,
      "retryCount": null
    },
    {
      "id": "cc0e8400-e29b-41d4-a716-446655440007",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "STEP",
      "stepName": "createRealm",
      "eventType": "UNDO_COMPLETED",
      "status": "ROLLED_BACK",
      "timestamp": "2025-11-03T20:10:53.456Z",
      "reason": null,
      "details": {
        "worker": "realm-worker",
        "durationMs": 2333,
        "message": "UNDO operation completed"
      },
      "createdBy": null,
      "serviceName": "realm-worker",
      "operationType": "UNDO",
      "durationMs": 2333,
      "retryCount": null
    },
    {
      "id": "dd0e8400-e29b-41d4-a716-446655440008",
      "executionId": "abc123",
      "orchName": "tenantCreation",
      "entityType": "ORCHESTRATION",
      "stepName": null,
      "eventType": "ROLLBACK_COMPLETED",
      "status": "ROLLED_BACK",
      "timestamp": "2025-11-03T20:10:54.789Z",
      "reason": null,
      "details": {
        "message": "All steps rolled back successfully"
      },
      "createdBy": null,
      "serviceName": null,
      "operationType": null,
      "durationMs": null,
      "retryCount": null
    }
  ]
}
```

---

## 💻 Usage Examples

### Frontend - Timeline Visualization

```typescript
interface AuditEvent {
  id: string;
  executionId: string;
  orchName: string;
  entityType: 'ORCHESTRATION' | 'STEP';
  stepName?: string;
  eventType: string;
  status?: string;
  timestamp: string;
  reason?: string;
  details?: Record<string, any>;
  serviceName?: string;
  operationType?: 'DO' | 'UNDO';
  durationMs?: number;
  retryCount?: number;
}

// Fetch timeline
const fetchTimeline = async (executionId: string) => {
  const response = await fetch(`/api/audit/${executionId}`);
  const timeline = await response.json();
  return timeline;
};

// Display in UI
const TimelineView = ({ executionId }: { executionId: string }) => {
  const [timeline, setTimeline] = useState<AuditTimeline | null>(null);

  useEffect(() => {
    fetchTimeline(executionId).then(setTimeline);
  }, [executionId]);

  return (
    <div>
      <h2>Execution Timeline</h2>
      <div className="timeline-stats">
        <span>Total Events: {timeline?.totalEvents}</span>
        <span>Failed Events: {timeline?.failedEvents}</span>
        <span>Retry Events: {timeline?.retryEvents}</span>
      </div>
      
      <div className="timeline">
        {timeline?.events.map(event => (
          <TimelineEvent key={event.id} event={event} />
        ))}
      </div>
    </div>
  );
};

const TimelineEvent = ({ event }: { event: AuditEvent }) => {
  const getEventIcon = (type: string) => {
    switch (type) {
      case 'ORCHESTRATION_STARTED': return '🚀';
      case 'STEP_STARTED': return '▶️';
      case 'STEP_COMPLETED': return '✅';
      case 'STEP_FAILED': return '❌';
      case 'RETRY_ATTEMPT': return '🔄';
      case 'UNDO_STARTED': return '↩️';
      case 'ROLLBACK_TRIGGERED': return '⚠️';
      default: return '•';
    }
  };

  return (
    <div className={`timeline-event ${event.eventType.toLowerCase()}`}>
      <div className="event-icon">{getEventIcon(event.eventType)}</div>
      <div className="event-content">
        <div className="event-time">{formatTime(event.timestamp)}</div>
        <div className="event-title">
          {event.eventType.replace(/_/g, ' ')}
          {event.stepName && ` - ${event.stepName}`}
        </div>
        {event.reason && (
          <div className="event-reason">⚠️ {event.reason}</div>
        )}
        {event.durationMs && (
          <div className="event-duration">⏱️ {event.durationMs}ms</div>
        )}
        {event.serviceName && (
          <div className="event-service">🔧 {event.serviceName}</div>
        )}
      </div>
    </div>
  );
};
```

---

## 🔧 Configuration

### Enable Async Processing

Ensure `@EnableAsync` is configured in your application:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "auditTaskExecutor")
    public Executor auditTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("audit-");
        executor.initialize();
        return executor;
    }
}
```

Update `AuditService` to use the executor:

```java
@Async("auditTaskExecutor")
public void recordEvent(AuditEvent event) {
    // ...
}
```

---

## 📈 Performance Considerations

### Asynchronous Recording
- Audit events are recorded asynchronously to avoid blocking orchestration flow
- Uses separate transaction to ensure audit doesn't fail orchestration
- Thread pool sized appropriately for audit workload

### Database Indexes
- 7 indexes created for optimal query performance
- Compound index on `(execution_id, timestamp)` for timeline queries
- JSONB column for flexible metadata without schema changes

### Cleanup Strategy
Consider implementing periodic cleanup:

```sql
-- Delete audit events older than 90 days
DELETE FROM audit_event 
WHERE timestamp < NOW() - INTERVAL '90 days';

-- Or archive to separate table
INSERT INTO audit_event_archive 
SELECT * FROM audit_event 
WHERE timestamp < NOW() - INTERVAL '90 days';
```

---

## 🚀 Deployment

### 1. Run Migration
```bash
psql -U postgres -d orchestrator_db -f migration-audit-events.sql
```

### 2. Verify Tables
```sql
\d audit_event
SELECT * FROM v_execution_timeline LIMIT 10;
```

### 3. Build & Deploy
```bash
mvn clean package -DskipTests
java -jar target/orchestrator-service-1.0.0.jar
```

### 4. Test API
```bash
# Start an orchestration
curl -X POST http://localhost:8080/api/orchestrations/tenantCreation/execute

# Get timeline (use the executionId from above)
curl http://localhost:8080/api/audit/{executionId}
```

---

## ✅ Summary

### What Was Implemented
- ✅ Complete audit event tracking system
- ✅ Asynchronous event recording
- ✅ Rich metadata with JSONB support
- ✅ REST API for timeline retrieval
- ✅ Database schema with indexes and views
- ✅ Integration with all orchestration flow points
- ✅ Support for DO and UNDO operations
- ✅ Retry tracking
- ✅ Rollback event capture

### Benefits
- 📊 Complete visibility into orchestration execution
- 🐛 Enhanced debugging capabilities
- 📈 Performance analysis with duration tracking
- 🔍 Detailed failure investigation
- 📝 Compliance and audit trail
- 🎯 Non-blocking, production-ready

**The integrated audit feature is now fully operational! 🎉**

