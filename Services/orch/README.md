# Orchestrator Service

A comprehensive Spring Boot microservice implementing event-driven orchestration with pluggable message brokers and self-healing capabilities.

## 🎯 Overview

The Orchestrator Service manages complex workflows where services register orchestration definitions via YAML. It supports both sequential and simultaneous execution patterns, step-level tracking, undo operations, and automatic self-healing when workers register later.

## 🏗️ Architecture

### Key Components
- **OrchestrationRegistryService**: Handles initiator and worker registration
- **OrchestrationExecutorService**: Manages workflow execution (sequential/simultaneous)
- **UndoService**: Handles rollback operations
- **SelfHealingService**: Automatically updates orchestration status when missing workers register
- **Pluggable Messaging**: Interface-based broker abstraction (Kafka implementation included)

### Database Schema
- `orchestration_template`: Stores orchestration definitions
- `orchestration_step_template`: Individual step definitions
- `worker_registration`: Maps workers to steps they can handle
- `registration_audit`: Tracks all registration attempts
- `orchestration_run`: Execution instances
- `orchestration_step_run`: Individual step execution tracking

## 🚀 Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven 3.6+

### Running with Docker Compose
```bash
# Build and start all services
docker-compose up --build

# The service will be available at http://localhost:8080
```

### Manual Setup
1. Start PostgreSQL and Kafka
2. Update `application.yml` with your database and Kafka settings
3. Run the application:
```bash
mvn clean install
java -jar target/orchestrator-service-1.0.0.jar
```

## 📖 Usage Guide

### 1. Register Orchestration (Initiator)

**HTTP Request:**
```bash
curl -X POST http://localhost:8080/api/orchestration/register \
  -H "Content-Type: application/json" \
  -H "X-Service-Name: tenant-service" \
  -d '{
    "orchName": "tenantCreation",
    "as": "initiator",
    "type": "sequential",
    "steps": [
      {"seq": 1, "name": "createRealm", "objectType": "String"},
      {"seq": 2, "name": "createClient", "objectType": "String"}
    ]
  }'
```

**Kafka Event (YAML):**
Send YAML to `orchestrator.registration` topic:
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

### 2. Register Worker

**HTTP Request:**
```bash
curl -X POST http://localhost:8080/api/orchestration/register \
  -H "Content-Type: application/json" \
  -H "X-Service-Name: realm-service" \
  -d '{
    "orchName": "tenantCreation",
    "as": "worker",
    "steps": [
      {"name": "createRealm", "objectType": "String"}
    ]
  }'
```

### 3. Execute Orchestration

**HTTP Request:**
```bash
curl -X POST http://localhost:8080/api/orchestration/execute/tenantCreation \
  -H "Content-Type: application/octet-stream" \
  -d '{"tenantId": "test-tenant", "realmName": "test-realm"}'
```

**Kafka Event:**
Send to `orchestrator.execution.start` topic:
```json
{
  "orchName": "tenantCreation",
  "payload": "eyJ0ZW5hbnRJZCI6ICJ0ZXN0In0="
}
```

### 4. Handle Step Responses

Workers respond to step execution by sending events to their assigned topics:
```json
{
  "flowId": "uuid-of-flow",
  "orchName": "tenantCreation",
  "stepName": "createRealm",
  "action": "DO",
  "metadata": {
    "status": "SUCCESS"
  }
}
```

For failures:
```json
{
  "flowId": "uuid-of-flow",
  "orchName": "tenantCreation",
  "stepName": "createRealm",
  "action": "DO",
  "metadata": {
    "status": "FAILED",
    "error": "Realm creation failed: Invalid configuration"
  }
}
```

### 5. Manual Undo

```bash
curl -X POST http://localhost:8080/api/orchestration/undo/{flowId}
```

## 🔧 Configuration

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orchestrator_db
    username: orchestrator_user
    password: orchestrator_pass
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: orchestrator-service
```

### Environment Variables
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers
- `SPRING_PROFILES_ACTIVE`: Active Spring profile

## 🔌 Pluggable Brokers

The service uses interface-based messaging abstraction. To add RabbitMQ support:

1. Implement the interfaces:
```java
@Component
public class RabbitMQMessagePublisher implements MessagePublisher {
    // Implementation
}
```

2. Update configuration to use RabbitMQ beans instead of Kafka

## 🏥 Self-Healing

The service automatically:
- Monitors orchestrations in FAILED/PENDING state
- Updates status to SUCCESS when missing workers register
- Runs every 60 seconds via scheduled task

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8080/api/orchestration/health
```

### Swagger UI
Available at: `http://localhost:8080/swagger-ui.html`

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn integration-test
```

Uses Testcontainers for PostgreSQL and Kafka.

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure PostgreSQL is running
   - Check connection string and credentials
   - Verify Flyway migrations completed

2. **Kafka Connection Issues**
   - Ensure Kafka and Zookeeper are running
   - Check bootstrap servers configuration
   - Verify topic creation permissions

3. **Orchestration Not Starting**
   - Check if all required workers are registered
   - Verify orchestration status in database
   - Check logs for validation errors

### Logs
```bash
# View application logs
docker-compose logs orchestrator-service

# Follow logs
docker-compose logs -f orchestrator-service
```

## 🔐 Security Considerations

- Service authentication via headers (X-Service-Name)
- Database credentials should use secrets management
- Kafka topics should have appropriate ACLs
- Consider TLS for production deployments

## 🚀 Production Deployment

1. Use environment-specific configuration
2. Enable authentication and authorization
3. Set up monitoring and alerting
4. Configure log aggregation
5. Use secrets management for credentials
6. Scale replicas based on load

## 📈 Performance Considerations

- Database connection pooling configured
- Kafka producer optimized for throughput
- Async processing for non-blocking operations
- Indexes on frequently queried columns

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit pull request

## 📝 License

Copyright 2025 - Orchestrator Service
