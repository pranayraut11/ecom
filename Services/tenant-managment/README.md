# Tenant Management Service

A comprehensive Spring Boot 3.x microservice for multi-tenant management using the "one schema per tenant" strategy with PostgreSQL.

## 🏗 Architecture Overview

This microservice implements a multi-tenant architecture where:
- **Master Schema (public)**: Stores tenant metadata in `public.tenants` table
- **Tenant Schemas**: Each tenant gets its own PostgreSQL schema with isolated data
- **Dynamic Routing**: Automatic schema routing based on tenant context
- **Schema Management**: Automated schema creation and migration using Flyway

## 🚀 Features

### Core Functionality
- ✅ Complete CRUD operations for tenant management
- ✅ Automatic PostgreSQL schema creation per tenant
- ✅ Flyway-based schema migrations
- ✅ Multi-tenant request routing via headers/domain
- ✅ Soft delete with audit trails
- ✅ Status management (ACTIVE, INACTIVE, SUSPENDED)

### Technical Features
- ✅ Spring Boot 3.x with Java 21
- ✅ Spring Data JPA with Hibernate
- ✅ MapStruct for DTO mapping
- ✅ Comprehensive validation
- ✅ OpenAPI/Swagger documentation
- ✅ Docker containerization
- ✅ Unit tests with high coverage
- ✅ Global exception handling

## 📁 Project Structure

```
com.example.tenantmanagement
├── TenantManagementApplication.java     # Main Spring Boot application
├── controller/
│   └── TenantController.java           # REST API endpoints
├── service/
│   ├── TenantService.java             # Service interface
│   └── TenantServiceImpl.java         # Business logic implementation
├── repository/
│   └── TenantRepository.java          # Data access layer
├── entity/
│   └── TenantEntity.java              # JPA entity for tenants
├── dto/
│   ├── TenantDTO.java                 # Response DTO
│   ├── TenantCreateRequest.java       # Create request DTO
│   ├── TenantUpdateRequest.java       # Update request DTO
│   ├── TenantStatusUpdateRequest.java # Status update DTO
│   └── ApiResponse.java               # Standardized response wrapper
├── config/
│   ├── MultiTenantConfig.java         # Multi-tenancy configuration
│   └── OpenApiConfig.java             # Swagger/OpenAPI config
├── multitenancy/
│   ├── TenantContext.java             # Thread-local tenant context
│   ├── TenantRoutingDataSource.java   # Dynamic data source routing
│   └── TenantInterceptor.java         # Request interceptor
├── exception/
│   ├── TenantNotFoundException.java
│   ├── TenantAlreadyExistsException.java
│   ├── SchemaCreationException.java
│   └── GlobalExceptionHandler.java    # Centralized error handling
└── util/
    ├── TenantMapper.java              # MapStruct mapper
    └── SchemaManager.java             # Schema operations utility
```

## 🔧 Prerequisites

- **Java 21** or higher
- **PostgreSQL 12+**
- **Maven 3.8+**
- **Docker** (optional, for containerized deployment)

## 🚀 Quick Start

### 1. Clone and Build
```bash
git clone <repository-url>
cd tenant-management
mvn clean install
```

### 2. Database Setup
```bash
# Create PostgreSQL database
createdb tenant_management

# Or use Docker
docker-compose up postgres
```

### 3. Run Application
```bash
# Using Maven
mvn spring-boot:run

# Or using Docker Compose
docker-compose up
```

### 4. Access Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## 📝 API Documentation

### Base URL
```
http://localhost:8080/api/v1/tenants
```

### Endpoints

#### 1. Create Tenant
```http
POST /api/v1/tenants
Content-Type: application/json

{
  "tenantName": "Acme Corp",
  "domain": "acme.example.com",
  "contactEmail": "admin@acme.example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tenant created successfully",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "tenantName": "Acme Corp",
    "schemaName": "tenant_acme_corp",
    "domain": "acme.example.com",
    "contactEmail": "admin@acme.example.com",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

#### 2. Get Tenant by ID
```http
GET /api/v1/tenants/{id}
```

#### 3. List All Tenants (with pagination)
```http
GET /api/v1/tenants?status=ACTIVE&page=0&size=20&sort=createdAt,desc
```

#### 4. Update Tenant
```http
PUT /api/v1/tenants/{id}
Content-Type: application/json

{
  "tenantName": "Updated Acme Corp",
  "contactEmail": "new-admin@acme.example.com"
}
```

#### 5. Update Tenant Status
```http
PATCH /api/v1/tenants/{id}/status
Content-Type: application/json

{
  "status": "SUSPENDED"
}
```

#### 6. Delete Tenant (Soft Delete)
```http
DELETE /api/v1/tenants/{id}
```

### Multi-Tenant Request Headers

When making requests to tenant-specific data, include one of these headers:

```http
# Option 1: Direct tenant ID
X-Tenant-ID: tenant_acme_corp

# Option 2: Tenant domain
X-Tenant-Domain: acme.example.com
```

## 🗄️ Database Schema

### Master Schema (public.tenants)
```sql
CREATE TABLE public.tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_name VARCHAR(100) NOT NULL UNIQUE,
    schema_name VARCHAR(100) NOT NULL UNIQUE,
    domain VARCHAR(255) NOT NULL UNIQUE,
    contact_email VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Tenant Schema Example
Each tenant gets their own schema with tables like:
- `users` - Tenant-specific users
- `products` - Tenant-specific products
- Additional business tables as needed

## 🧪 Testing

### Run Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Test coverage report
mvn jacoco:report
```

### Test Data Examples
```bash
# Create test tenant
curl -X POST http://localhost:8080/api/v1/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "tenantName": "Test Company",
    "domain": "test.example.com", 
    "contactEmail": "admin@test.example.com"
  }'
```

## 🐳 Docker Deployment

### Build and Run
```bash
# Build image
docker build -t tenant-management:latest .

# Run with Docker Compose
docker-compose up -d

# Scale service
docker-compose up -d --scale tenant-management-service=3
```

### Environment Variables
```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/tenant_management
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tenant_management
    username: postgres
    password: password
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
  flyway:
    enabled: true
    locations: classpath:db/migration/master
    schemas: public
```

## 🚨 Error Handling

The service provides standardized error responses:

```json
{
  "success": false,
  "message": "Tenant not found with ID: 123e4567-e89b-12d3-a456-426614174000",
  "data": null
}
```

Common HTTP status codes:
- `201` - Created (successful tenant creation)
- `200` - OK (successful operations)
- `400` - Bad Request (validation errors)
- `404` - Not Found (tenant not found)
- `409` - Conflict (duplicate tenant/domain)
- `500` - Internal Server Error

## 📊 Monitoring & Health Checks

### Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 🔒 Security Considerations

- **Input Validation**: All inputs are validated using Jakarta Validation
- **SQL Injection Prevention**: Using parameterized queries and JPA
- **Schema Isolation**: Each tenant has isolated database schema
- **Soft Delete**: Data is never permanently deleted, only marked as deleted

## 🔄 Multi-Tenancy Flow

1. **Request arrives** with tenant identification (header/domain)
2. **TenantInterceptor** resolves tenant ID and sets context
3. **TenantRoutingDataSource** routes to appropriate schema
4. **Business logic** executes in tenant-specific context
5. **Response returned** with tenant-isolated data
6. **Context cleared** after request completion

## 🎯 Next Steps

- [ ] Add authentication/authorization (Spring Security)
- [ ] Implement tenant-specific caching
- [ ] Add monitoring and logging enhancements
- [ ] Create tenant data backup/restore functionality
- [ ] Add tenant usage analytics and billing integration

## 📞 Support

For questions or issues:
- Check the API documentation at `/swagger-ui.html`
- Review the logs for detailed error information
- Contact the development team

---

**Built with ❤️ using Spring Boot 3.x, Java 21, and PostgreSQL**
