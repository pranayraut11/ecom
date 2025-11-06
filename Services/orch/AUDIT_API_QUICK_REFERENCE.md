# Audit API Quick Reference

## 🎯 Endpoint

```
GET /api/audit/{executionId}
```

## 📋 Query Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `executionId` | Path | Yes | Execution ID (flowId) | `abc123` |
| `eventType` | Query | No | Filter by event type | `STEP_FAILED` |
| `status` | Query | No | Filter by status | `FAILED` |
| `from` | Query | No | Start timestamp (ISO 8601) | `2025-11-03T20:00:00` |
| `to` | Query | No | End timestamp (ISO 8601) | `2025-11-03T21:00:00` |

## 🔑 Event Types

### Orchestration Events
- `ORCHESTRATION_STARTED`
- `ORCHESTRATION_COMPLETED`
- `ORCHESTRATION_FAILED`

### Step Events (DO)
- `STEP_STARTED`
- `STEP_COMPLETED`
- `STEP_FAILED`

### Retry Events
- `RETRY_ATTEMPT`
- `RETRY_EXHAUSTED`

### Rollback Events
- `ROLLBACK_TRIGGERED` - When rollback is triggered due to failure
- `ROLLBACK_STARTED` - When rollback process actually begins
- `UNDO_STARTED`
- `UNDO_COMPLETED`
- `UNDO_FAILED`
- `ROLLBACK_COMPLETED`

## 📝 Example Requests

### Get Full Timeline
```bash
curl http://localhost:8080/api/audit/abc123
```

### Get Failed Events Only
```bash
curl "http://localhost:8080/api/audit/abc123?eventType=STEP_FAILED"
```

### Get Events in Time Range
```bash
curl "http://localhost:8080/api/audit/abc123?from=2025-11-03T20:00:00&to=2025-11-03T21:00:00"
```

### Get Failed Steps with Status
```bash
curl "http://localhost:8080/api/audit/abc123?eventType=STEP_FAILED&status=FAILED"
```

## 📤 Response Structure

```json
{
  "executionId": "string",
  "orchName": "string",
  "totalEvents": "integer",
  "failedEvents": "integer",
  "retryEvents": "integer",
  "events": [
    {
      "id": "UUID",
      "executionId": "string",
      "orchName": "string",
      "entityType": "ORCHESTRATION | STEP",
      "stepName": "string?",
      "eventType": "enum",
      "status": "string?",
      "timestamp": "ISO 8601",
      "reason": "string?",
      "details": "object?",
      "createdBy": "string?",
      "serviceName": "string?",
      "operationType": "DO | UNDO?",
      "durationMs": "long?",
      "retryCount": "integer?"
    }
  ]
}
```

## 🎨 Frontend TypeScript

```typescript
interface AuditTimeline {
  executionId: string;
  orchName: string;
  totalEvents: number;
  failedEvents: number;
  retryEvents: number;
  events: AuditEvent[];
}

interface AuditEvent {
  id: string;
  executionId: string;
  orchName: string;
  entityType: 'ORCHESTRATION' | 'STEP';
  stepName?: string;
  eventType: AuditEventType;
  status?: string;
  timestamp: string;
  reason?: string;
  details?: Record<string, any>;
  createdBy?: string;
  serviceName?: string;
  operationType?: 'DO' | 'UNDO';
  durationMs?: number;
  retryCount?: number;
}

type AuditEventType = 
  | 'ORCHESTRATION_STARTED'
  | 'ORCHESTRATION_COMPLETED'
  | 'ORCHESTRATION_FAILED'
  | 'STEP_STARTED'
  | 'STEP_COMPLETED'
  | 'STEP_FAILED'
  | 'RETRY_ATTEMPT'
  | 'RETRY_EXHAUSTED'
  | 'ROLLBACK_TRIGGERED'
  | 'ROLLBACK_STARTED'
  | 'UNDO_STARTED'
  | 'UNDO_COMPLETED'
  | 'UNDO_FAILED'
  | 'ROLLBACK_COMPLETED';

// Usage
const fetchTimeline = async (executionId: string): Promise<AuditTimeline> => {
  const response = await fetch(`/api/audit/${executionId}`);
  return response.json();
};
```

## 🗄️ Database Queries

### Get Timeline
```sql
SELECT * FROM audit_event
WHERE execution_id = 'abc123'
ORDER BY timestamp ASC;
```

### Using View
```sql
SELECT * FROM v_execution_timeline
WHERE execution_id = 'abc123';
```

### Count Events by Type
```sql
SELECT event_type, COUNT(*)
FROM audit_event
WHERE execution_id = 'abc123'
GROUP BY event_type;
```

### Find Failed Steps
```sql
SELECT step_name, reason, timestamp
FROM audit_event
WHERE execution_id = 'abc123'
AND event_type = 'STEP_FAILED'
ORDER BY timestamp;
```

### Calculate Average Duration
```sql
SELECT 
  step_name,
  AVG(duration_ms) as avg_duration_ms
FROM audit_event
WHERE execution_id = 'abc123'
AND duration_ms IS NOT NULL
GROUP BY step_name;
```

## 🔍 Common Use Cases

### 1. Debug Failed Execution
```bash
# Get all failed events
curl "http://localhost:8080/api/audit/{executionId}?status=FAILED"
```

### 2. Track Retry Behavior
```bash
# Get all retry events
curl "http://localhost:8080/api/audit/{executionId}?eventType=RETRY_ATTEMPT"
```

### 3. Analyze Rollback
```bash
# Get all UNDO operations
curl "http://localhost:8080/api/audit/{executionId}" | jq '.events[] | select(.operationType == "UNDO")'
```

### 4. Performance Analysis
```typescript
const calculatePerformance = (timeline: AuditTimeline) => {
  const stepDurations = timeline.events
    .filter(e => e.durationMs != null)
    .map(e => ({
      step: e.stepName,
      duration: e.durationMs,
      type: e.operationType
    }));
  
  return {
    totalSteps: stepDurations.length,
    avgDuration: average(stepDurations.map(d => d.duration)),
    slowestStep: max(stepDurations, d => d.duration)
  };
};
```

## 📊 HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | Timeline retrieved successfully |
| 404 | Execution not found or no events |
| 500 | Server error |

## 🚀 Quick Start

1. **Run Migration**
   ```bash
   psql -U postgres -d orchestrator_db -f migration-audit-events.sql
   ```

2. **Start Application**
   ```bash
   mvn spring-boot:run
   ```

3. **Execute Orchestration**
   ```bash
   # Your normal orchestration execution
   ```

4. **View Timeline**
   ```bash
   curl http://localhost:8080/api/audit/{executionId} | jq
   ```

---

**All orchestration executions are now fully auditable!** 🎉

