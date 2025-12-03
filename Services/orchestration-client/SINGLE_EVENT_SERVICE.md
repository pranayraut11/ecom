# Single Event Service

## Overview
`SingleEventServiceImpl` is a specialized implementation of `OrchestrationService` interface designed for handling single event processing using a shared topic approach. It provides a simpler alternative to the full orchestration service for scenarios where shared topic patterns are needed.

## Conditional Activation
The service is **conditionally activated** based on the `sharedTopic` property in `orchestrations.yml`:

```yaml
sharedTopic: true  # Set to true to enable SingleEventServiceImpl
orchestrations:
  # ... your orchestrations
```

The `@Conditional(SharedTopicCondition.class)` annotation ensures that:
- The service bean is only created when `sharedTopic: true` is set in `orchestrations.yml`
- No impact on existing code if the feature is not enabled
- Clean separation of concerns
- When enabled, it provides an alternative implementation of `OrchestrationService`

## Features

### 1. Start Orchestration
Start an orchestration with automatic header enrichment:
```java
@Autowired
private OrchestrationService orchestrationService;  // Will be SingleEventServiceImpl when sharedTopic: true

ExecutionMessage message = ExecutionMessage.builder()
    .payload(yourData)
    .headers(new HashMap<>())
    .build();

orchestrationService.startOrchestration(message, "tenantCreation", "orchestrator.event");
```

### 2. Register Orchestration
Register an orchestration definition:
```java
OrchestrationConfig.Orchestration orchestration = ...; // your orchestration config

// Register to specific topic
orchestrationService.register(orchestration, "custom.topic.name");

// Register to default topic (orchestrator.event)
orchestrationService.register(orchestration);
```

### 3. Do Next Step
```java
// Send to specific topic
orchestrationService.doNext(message, "custom.topic");

// Send to default topic (orchestrator.event)
orchestrationService.doNext(message);
```

### 4. Undo Next Step
```java
// Send to specific topic
orchestrationService.undoNext(message, "response.topic");

// Send to default topic (orchestrator.event.response)
orchestrationService.undoNext(message);
```

### 5. Fail Step
```java
// Send to specific topic
orchestrationService.failStep(message, "response.topic");

// Send to default topic (orchestrator.event.response)
orchestrationService.failStep(message);
```

## Default Topics
- **Event Topic**: `orchestrator.event`
- **Response Topic**: `orchestrator.event.response`

## Automatic Header Enrichment
The service automatically adds/updates the following headers:
- `flowId`: Unique identifier for the event flow
- `eventType`: Type of the event being processed
- `source`: Application name from `spring.application.name`
- `serviceName`: Same as source
- `X-Service-Name`: Same as source
- `topic`: Target topic name
- `contentType`: Set to "application/json"
- `status`: Boolean status (true for success, false for failure)
- `responseType`: "SUCCESS" or "FAILURE"

## Example Usage

### Basic Orchestration with Shared Topic
```java
@Service
public class OrderService {
    
    @Autowired
    private OrchestrationService orchestrationService;  // SingleEventServiceImpl when sharedTopic: true
    
    public void createOrder(Order order) {
        try {
            // Create execution message
            ExecutionMessage message = ExecutionMessage.builder()
                .payload(order)
                .headers(new HashMap<>())
                .build();
                
            // Start orchestration
            orchestrationService.startOrchestration(message, "orderCreation");
            
            // Continue to next step
            orchestrationService.doNext(message);
            
        } catch (Exception e) {
            // Handle failure
            ExecutionMessage failureMessage = ExecutionMessage.builder()
                .payload(order)
                .headers(new HashMap<>())
                .build();
            failureMessage.getHeaders().put("errorMessage", e.getMessage());
            
            orchestrationService.failStep(failureMessage);
        }
    }
}
```

## Comparison with OrchestrationServiceImpl

| Feature | OrchestrationServiceImpl | SingleEventServiceImpl |
|---------|-------------------------|------------------------|
| Interface | OrchestrationService | OrchestrationService |
| Full workflow orchestration | ✅ | ✅ |
| Single event handling | ✅ | ✅ |
| Step sequencing | ✅ | ✅ |
| Automatic rollback | ✅ | ✅ |
| Shared topic pattern | ❌ | ✅ |
| Conditional activation | ❌ | ✅ |
| Default activation | ✅ | ❌ (requires sharedTopic: true) |

## Configuration

### Enable the Service
In `orchestrations.yml`:
```yaml
sharedTopic: true
orchestrations:
  - orchestrationName: myOrchestration
    as: worker
    steps:
      # ... your steps
```

### Customize Application Name
In `application.yml` or `application.properties`:
```yaml
spring:
  application:
    name: my-service-name
```

## Architecture

```
┌─────────────────────────┐
│   Client Application    │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│  OrchestrationService   │
│  (Interface)            │
└───────────┬─────────────┘
            │
    ┌───────┴────────┐
    │                │
    ▼                ▼
┌────────────┐  ┌──────────────────────┐
│Orchestration│  │SingleEventServiceImpl│
│ServiceImpl │  │(Conditional Bean)    │
│(Default)   │  │sharedTopic: true     │
└─────┬──────┘  └──────────┬───────────┘
      │                    │
      └────────┬───────────┘
               ▼
┌─────────────────────────┐
│  KafkaEventPublisher    │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│    Kafka Broker         │
└─────────────────────────┘
```

## Bean Selection Logic

- **When `sharedTopic: false` or not set**: `OrchestrationServiceImpl` is used (default)
- **When `sharedTopic: true`**: `SingleEventServiceImpl` is used (alternative implementation)
- Both implement the same `OrchestrationService` interface
- Client code doesn't need to change, just inject `OrchestrationService`

## Error Handling
All methods throw `IllegalStateException` on errors with appropriate error messages:
- Event processing failures
- Event send failures
- Success/failure response send failures

## Thread Safety
The service is thread-safe as it uses:
- Stateless operations
- Thread-safe Kafka publisher
- Immutable configuration

## Notes
- The service is automatically registered as a Spring bean only when `sharedTopic: true`
- Implements the same `OrchestrationService` interface as `OrchestrationServiceImpl`
- Uses the same `KafkaEventPublisher` as `OrchestrationServiceImpl`
- Follows the same message structure (`ExecutionMessage`) for consistency
- Provides an alternative implementation pattern using shared topics
- When enabled, Spring will inject `SingleEventServiceImpl` instead of `OrchestrationServiceImpl`
- Client code remains unchanged - just inject `OrchestrationService` interface

