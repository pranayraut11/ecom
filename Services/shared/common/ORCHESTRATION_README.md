# Orchestration Service

An event-driven, platform-agnostic orchestration system that reads YAML configuration files, converts them to JSON, and publishes events to specified topics.

## Features

- **Platform Agnostic**: Supports multiple event publishers (Spring Events, Kafka, etc.)
- **YAML to JSON Conversion**: Automatically converts YAML orchestration configs to JSON
- **Health Checks**: Validates connections before sending events
- **File Validation**: Checks file existence and content validity
- **Async Processing**: Non-blocking event processing
- **Extensible**: Easy to add new event publishers

## Usage

### 1. Basic Usage via REST API

```bash
# Process orchestration file and publish to topic
POST /api/orchestration/process?yamlFilePath=classpath:orchestrator.yml&topicName=tenant-creation

# Health check
GET /api/orchestration/health
```

### 2. Programmatic Usage

```java
@Autowired
private OrchestrationHelper orchestrationHelper;

// Process from classpath
orchestrationHelper.processFromClasspath("orchestrator.yml", "tenant-creation");

// Process from file system
orchestrationHelper.processFromFileSystem("/path/to/orchestrator.yml", "tenant-creation");

// Process default orchestrator.yml
orchestrationHelper.processDefaultOrchestration("tenant-creation");
```

### 3. Direct Service Usage

```java
@Autowired
private OrchestrationService orchestrationService;

orchestrationService.processOrchestrationFile("classpath:orchestrator.yml", "tenant-creation")
    .thenRun(() -> log.info("Processing completed"))
    .exceptionally(throwable -> {
        log.error("Processing failed", throwable);
        return null;
    });
```

## YAML Configuration Format

```yaml
orchName: tenantCreation
as: initiator
type: sequential
steps:
  - seq: 1
    name: createRealm
    objectType: String
  - seq: 2
    name: createClient
    objectType: String
```

### Required Fields
- `orchName`: Name of the orchestration
- `steps`: Array of orchestration steps
- `steps[].seq`: Step sequence number
- `steps[].name`: Step name

### Optional Fields
- `as`: Role (e.g., initiator, participant)
- `type`: Orchestration type (e.g., sequential, parallel)
- `steps[].objectType`: Type of object for the step

## Event Publishers

### Spring Event Publisher (Default)
- No external dependencies
- Uses Spring's ApplicationEventPublisher
- Always available when Spring context is running

### Kafka Event Publisher
- Requires Kafka configuration
- Only active when `orchestrator.event.publisher.type=kafka`
- Includes connection health checks

## Configuration

Add to your `application.yml`:

```yaml
orchestrator:
  event:
    publisher:
      type: spring_event  # Options: spring_event, kafka
  kafka:
    bootstrap-servers: localhost:9092
  async:
    enabled: true
```

## Event Handling

Listen to orchestration events:

```java
@EventListener
public void handleOrchestrationEvent(OrchestrationEvent event) {
    log.info("Received event: {} for orchestration: {}", 
        event.getEventType(), event.getOrchestrationName());
    
    // Process the event
    Map<String, Object> payload = event.getPayload();
    // Your business logic here
}
```

## Extending with New Publishers

Create a new event publisher:

```java
@Component
public class RabbitMQEventPublisher implements EventPublisher {
    
    @Override
    public CompletableFuture<Void> publishEvent(OrchestrationEvent event) {
        // Implement RabbitMQ publishing logic
    }
    
    @Override
    public boolean isConnectionHealthy() {
        // Implement RabbitMQ health check
    }
    
    @Override
    public String getPublisherType() {
        return "RABBITMQ";
    }
}
```

## Error Handling

The system includes comprehensive validation:
- File existence validation
- YAML syntax validation
- Required field validation
- Connection health checks
- Async error handling

## Integration

Include in your Spring Boot application:

```java
@Import(ImportCommonClasses.class)
@SpringBootApplication
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```
