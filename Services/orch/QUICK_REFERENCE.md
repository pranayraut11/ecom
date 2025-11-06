# DO/UNDO Orchestration - Quick Reference Guide

## Quick Start

### 1. Run Database Migration
```bash
psql -U postgres -d orchestrator_db -f migration-do-undo-topics.sql
```

### 2. Register Orchestration (Initiator)
```yaml
orchestrationName: "tenantCreation"
as: "initiator"
type: "sequential"  # or "parallel"
steps:
  - seq: 1
    name: "createRealm"
    objectType: "String"
  - seq: 2
    name: "createClient"
    objectType: "String"
```

### 3. Worker Implementation

#### Listen to DO Topic
```java
@KafkaListener(topics = "orchestrator.tenantCreation.createRealm.do")
public void handleDo(ExecutionMessage message) {
    try {
        // Perform operation
        String result = createRealm(message.getPayload());
        
        // Send success response
        sendResponse(message, "DO", true, null);
    } catch (Exception e) {
        // Send failure response
        sendResponse(message, "DO", false, e.getMessage());
    }
}
```

#### Listen to UNDO Topic
```java
@KafkaListener(topics = "orchestrator.tenantCreation.createRealm.undo")
public void handleUndo(ExecutionMessage message) {
    try {
        // Rollback operation
        deleteRealm(message.getPayload());
        
        // Send success response
        sendResponse(message, "UNDO", true, null);
    } catch (Exception e) {
        // Send failure response
        sendResponse(message, "UNDO", false, e.getMessage());
    }
}
```

#### Send Response
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

## Topic Names

| Step Name | DO Topic | UNDO Topic |
|-----------|----------|------------|
| createRealm | `orchestrator.{orch}.createRealm.do` | `orchestrator.{orch}.createRealm.undo` |
| createClient | `orchestrator.{orch}.createClient.do` | `orchestrator.{orch}.createClient.undo` |

## Status Flow

### DO Operations
```
PENDING → IN_PROGRESS → DO_SUCCESS → (next step)
                    ↓ (fail)
                    DO_FAIL → (retry) → IN_PROGRESS
                           ↓ (exhausted)
                    RETRY_EXHAUSTED → UNDO Flow
```

### UNDO Operations
```
DO_SUCCESS → UNDOING → UNDO_SUCCESS → (previous step)
                   ↓ (fail)
                   UNDO_FAIL → (retry) → UNDOING
                            ↓ (exhausted)
                       UNDO_FAIL (final)
```

## Configuration

### Default Retry Settings
- **Max Retries**: 3 attempts per step
- **Applies to**: Both DO and UNDO operations
- **Customizable**: Set in orchestration registration

### Customizing Retries
```yaml
# In step template (future enhancement)
steps:
  - seq: 1
    name: "createRealm"
    objectType: "String"
    maxRetries: 5  # Override default
```

## API Endpoints

### Start Orchestration
```bash
POST /api/orchestrations/execute
{
  "orchestrationName": "tenantCreation",
  "payload": {...}
}
```

### Check Status
```bash
GET /api/orchestrations/history?orchName=tenantCreation
```

### Get Details
```bash
GET /api/orchestrations/details/{flowId}
```

## Database Queries

### Check Step Status
```sql
SELECT 
    flow_id,
    step_name,
    status,
    retry_count,
    max_retries,
    error_message
FROM orchestration_step_run
WHERE flow_id = 'your-flow-id'
ORDER BY seq;
```

### Find Failed Steps
```sql
SELECT * FROM orchestration_step_run
WHERE status IN ('RETRY_EXHAUSTED', 'DO_FAIL', 'UNDO_FAIL')
AND created_at > NOW() - INTERVAL '1 hour';
```

### Monitor Retry Rates
```sql
SELECT 
    step_name,
    AVG(retry_count) as avg_retries,
    MAX(retry_count) as max_retries,
    COUNT(*) as total_executions
FROM orchestration_step_run
GROUP BY step_name;
```

## Debugging

### Enable Debug Logs
```yaml
logging:
  level:
    com.ecom.orchestrator.service.DoOperationHandler: DEBUG
    com.ecom.orchestrator.service.UndoOperationHandler: DEBUG
```

### Check Message Headers
```java
log.info("Received message headers: {}", message.getHeaders());
```

### Verify Topic Creation
```bash
kafka-topics --list --bootstrap-server localhost:9092 | grep orchestrator
```

## Common Issues

### Issue: Step not executing
**Check**: 
1. Worker listening to correct DO topic?
2. Topic created in Kafka?
3. Message headers correct?

### Issue: Retry not working
**Check**:
1. Response includes `status` header?
2. `maxRetries` set correctly?
3. Worker sending proper response?

### Issue: UNDO not triggered
**Check**:
1. DO operation marked as DO_FAIL?
2. Retries exhausted?
3. UNDO topic exists?

## Best Practices

1. ✅ **Make operations idempotent**
2. ✅ **Store state needed for UNDO**
3. ✅ **Handle "already deleted" gracefully**
4. ✅ **Return detailed error messages**
5. ✅ **Test both success and failure paths**
6. ✅ **Monitor retry rates**
7. ✅ **Set appropriate timeouts**
8. ✅ **Log all operations**

## Testing Scenarios

### Test DO Success Path
1. Start orchestration
2. Verify DO message received
3. Send success response
4. Verify next step triggered

### Test DO Retry
1. Start orchestration
2. Send failure response
3. Verify retry attempt
4. Repeat until max retries
5. Verify UNDO triggered

### Test UNDO Path
1. Complete steps 1-2 successfully
2. Fail step 3
3. Verify UNDO for step 2
4. Verify UNDO for step 1
5. Verify final status UNDONE

## Monitoring Checklist

- [ ] DO success rate > 95%
- [ ] Average retry count < 1
- [ ] UNDO trigger rate < 5%
- [ ] UNDO success rate > 99%
- [ ] No steps stuck in IN_PROGRESS > 5 minutes
- [ ] Response time < 100ms per step

## Support

For detailed documentation, see:
- `DO-UNDO-ORCHESTRATION.md` - Full technical documentation
- `IMPLEMENTATION_SUMMARY.md` - Implementation details
- `BUILD_SUMMARY.md` - Build and deployment guide

## Summary

This system provides **distributed transaction-like guarantees** through:
- Automatic retry on failure
- Automatic rollback (UNDO) when retries exhausted
- Granular status tracking
- Sequential and parallel execution support
- Backward compatibility

