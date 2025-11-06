# Orchestration Examples - DO/UNDO System

This directory contains example configurations and implementations for the DO/UNDO orchestration system.

## 📋 Table of Contents

- [Registration Examples](#registration-examples)
- [Worker Implementation](#worker-implementation)
- [Execution Examples](#execution-examples)
- [Response Format](#response-format)

---

## Registration Examples

### Initiator Registration

**Purpose**: Register an orchestration flow (owner service)

**Files**:
- `initiator-registration.yaml` - Sequential orchestration example
- `initiator-registration.json` - JSON format for Kafka messages
- `parallel-initiator-registration.yaml` - Parallel orchestration example

**When to use Sequential vs Parallel**:

| Type | Use Case | Example |
|------|----------|---------|
| **Sequential** | Steps depend on each other | User creation → Email send → Permission assignment |
| **Parallel** | Independent steps | Create profile + Send email + Setup preferences (simultaneously) |

**Sending Registration**:

```bash
# Using Kafka Console Producer
kafka-console-producer --broker-list localhost:9092 \
  --topic orchestrator.registration \
  < initiator-registration.json

# Using REST API (if available)
curl -X POST http://localhost:8080/api/orchestration/register \
  -H "Content-Type: application/json" \
  -d @initiator-registration.json
```

---

### Worker Registration

**Purpose**: Register as a worker for specific steps

**Files**:
- `worker-registration.yaml` - Worker registration example
- `worker-registration.json` - JSON format for Kafka messages

**Sending Registration**:

```bash
kafka-console-producer --broker-list localhost:9092 \
  --topic orchestrator.registration \
  < worker-registration.json
```

---

## Worker Implementation

**File**: `worker-implementation-example.java`

This file provides a complete example of how to implement a worker service that handles both DO and UNDO operations.

### Key Components

#### 1. DO Operation Listener
```java
@KafkaListener(topics = "orchestrator.tenantCreation.createRealm.do")
public void handleCreateRealmDo(ExecutionMessage message) {
    // Execute forward operation
    // Store state for potential UNDO
    // Send response
}
```

#### 2. UNDO Operation Listener
```java
@KafkaListener(topics = "orchestrator.tenantCreation.createRealm.undo")
public void handleCreateRealmUndo(ExecutionMessage message) {
    // Retrieve stored state
    // Execute rollback operation
    // Send response
}
```

#### 3. Response Handler
```java
private void sendResponse(ExecutionMessage message, String action, 
                         boolean success, String errorMessage) {
    // Construct response with headers
    // Send to orchestrator.response.result topic
}
```

### Implementation Checklist

- [ ] Create DO listener for each step
- [ ] Create UNDO listener for each step
- [ ] Implement state storage (Redis/Database)
- [ ] Handle idempotency (DO: "already exists", UNDO: "already deleted")
- [ ] Send proper responses with action field
- [ ] Add error handling and logging
- [ ] Add unit tests
- [ ] Add integration tests

---

## Execution Examples

### Starting an Orchestration

**File**: `orchestration-event.json`

```bash
# Send to execution.start topic
kafka-console-producer --broker-list localhost:9092 \
  --topic orchestrator.execution.start \
  < orchestration-event.json
```

**Using REST API**:
```bash
curl -X POST http://localhost:8080/api/orchestrations/execute \
  -H "Content-Type: application/json" \
  -d '{
    "orchestrationName": "tenantCreation",
    "payload": {
      "tenantName": "acme-corp",
      "domain": "acme.example.com"
    }
  }'
```

---

## Response Format

### DO Response

Worker sends this to `orchestrator.response.result` topic after completing a DO operation:

```json
{
  "headers": {
    "flowId": "550e8400-e29b-41d4-a716-446655440000",
    "stepName": "createRealm",
    "action": "DO",
    "status": true,
    "errorMessage": null
  },
  "payload": {
    "realmId": "realm-123",
    "tenantName": "acme-corp"
  }
}
```

### UNDO Response

Worker sends this after completing an UNDO operation:

```json
{
  "headers": {
    "flowId": "550e8400-e29b-41d4-a716-446655440000",
    "stepName": "createRealm",
    "action": "UNDO",
    "status": true,
    "errorMessage": null
  },
  "payload": {
    "realmId": "realm-123",
    "deleted": true
  }
}
```

### Failure Response

When an operation fails:

```json
{
  "headers": {
    "flowId": "550e8400-e29b-41d4-a716-446655440000",
    "stepName": "createRealm",
    "action": "DO",
    "status": false,
    "errorMessage": "Realm with name 'acme-corp' already exists"
  },
  "payload": {
    "tenantName": "acme-corp"
  }
}
```

---

## Topics Created

For orchestration `tenantCreation` with steps `createRealm` and `createClient`:

### DO Topics
```
orchestrator.tenantCreation.createRealm.do
orchestrator.tenantCreation.createClient.do
```

### UNDO Topics
```
orchestrator.tenantCreation.createRealm.undo
orchestrator.tenantCreation.createClient.undo
```

### Response Topic
```
orchestrator.response.result
```

### Registration Topic
```
orchestrator.registration
```

### Execution Topic
```
orchestrator.execution.start
```

---

## Flow Diagrams

### Sequential Flow (Success)

```
1. Orchestrator sends to: orchestrator.tenantCreation.createRealm.do
2. Worker executes and responds: DO success
3. Orchestrator sends to: orchestrator.tenantCreation.createClient.do
4. Worker executes and responds: DO success
5. Orchestration COMPLETED
```

### Sequential Flow (with Failure and Rollback)

```
1. Orchestrator sends to: orchestrator.tenantCreation.createRealm.do
2. Worker executes and responds: DO success
3. Orchestrator sends to: orchestrator.tenantCreation.createClient.do
4. Worker executes and responds: DO failure
5. Orchestrator retries (attempt 2): DO failure
6. Orchestrator retries (attempt 3): DO failure
7. Retry exhausted, trigger UNDO
8. Orchestrator sends to: orchestrator.tenantCreation.createRealm.undo
9. Worker executes and responds: UNDO success
10. Orchestration UNDONE
```

### Parallel Flow

```
1. Orchestrator sends simultaneously:
   - orchestrator.userOnboarding.createUserProfile.do
   - orchestrator.userOnboarding.sendWelcomeEmail.do
   - orchestrator.userOnboarding.setupDefaultPreferences.do
   
2. All workers execute in parallel

3. If all succeed: COMPLETED
   If any fails after retry: UNDO all successful steps in parallel
```

---

## Testing

### Test DO Success
```bash
# 1. Register orchestration
kafka-console-producer --broker-list localhost:9092 \
  --topic orchestrator.registration < initiator-registration.json

# 2. Start execution
kafka-console-producer --broker-list localhost:9092 \
  --topic orchestrator.execution.start < orchestration-event.json

# 3. Worker sends success response
# Check logs for DO_SUCCESS status
```

### Test Retry Mechanism
```bash
# 1. Start execution
# 2. Worker sends failure response 3 times
# 3. Verify RETRY_EXHAUSTED status
# 4. Verify UNDO triggered
```

### Test UNDO
```bash
# 1. Complete step 1 successfully
# 2. Fail step 2 after retries
# 3. Verify step 1 UNDO triggered
# 4. Verify final status: UNDONE
```

---

## Best Practices

### For Initiators
1. ✅ Use sequential for dependent steps
2. ✅ Use parallel for independent steps
3. ✅ Set appropriate retry counts per step
4. ✅ Keep orchestration names unique
5. ✅ Document step dependencies

### For Workers
1. ✅ Make operations idempotent
2. ✅ Store state with flowId as key
3. ✅ Handle "already exists" gracefully in DO
4. ✅ Handle "not found" gracefully in UNDO
5. ✅ Always send response (success or failure)
6. ✅ Include detailed error messages
7. ✅ Log with flowId for traceability
8. ✅ Set TTL for stored state (e.g., 24 hours)

---

## Troubleshooting

### Worker not receiving messages
- Check if topics are created: `kafka-topics --list --bootstrap-server localhost:9092`
- Verify worker is subscribed to correct topics
- Check Kafka consumer group status

### Step stuck in IN_PROGRESS
- Check if worker sent response
- Verify response topic is correct
- Check for network issues
- Implement timeout mechanism

### UNDO not triggered
- Verify DO operation failed
- Check retry count exhausted
- Verify UNDO topic exists
- Check orchestrator logs

---

## Additional Resources

- [DO-UNDO-ORCHESTRATION.md](../DO-UNDO-ORCHESTRATION.md) - Complete technical guide
- [QUICK_REFERENCE.md](../QUICK_REFERENCE.md) - Quick start guide
- [DEPLOYMENT_READY.md](../DEPLOYMENT_READY.md) - Deployment instructions

---

**Last Updated**: November 1, 2025  
**Version**: 1.0.0  
**Status**: ✅ Production Ready

