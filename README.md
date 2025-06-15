# E-Commerce Platform

A comprehensive e-commerce solution with microservices architecture.

## Overview

This project is a full-featured e-commerce platform built using microservices architecture. It provides a complete solution for online shopping, including user management, product catalog, cart management, order processing, payment integration, and more.

![Architecture Diagram](https://via.placeholder.com/800x400?text=E-Commerce+Platform+Architecture)

## Architecture

The application follows a microservices architecture with the following components:

- **Frontend**: Angular-based UI application
- **Backend Services**: Multiple Spring Boot microservices
- **Authentication**: Keycloak for identity and access management
- **Databases**: MongoDB and PostgreSQL
- **Caching**: Redis
- **Storage**: MinIO for file storage
- **Service Discovery**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Message Broker**: Kafka for event-driven communication
- **Observability**: Grafana, Prometheus, Loki, and Tempo for monitoring and tracing

## Key Features

- User registration and authentication
- Product catalog with categories and search functionality
- Shopping cart management
- Order processing and tracking
- Multiple payment gateway integrations
- User profile management
- Address management
- Seller dashboard with inventory management
- Notification system
- Reviews and ratings
- Responsive design for mobile and desktop

## Services

| Service | Description | Technologies |
|---------|-------------|--------------|
| User Service | Handles user management, authentication, and authorization | Spring Boot, Keycloak, PostgreSQL |
| Product Service | Manages product catalog and inventory | Spring Boot, MongoDB |
| Cart Service | Manages shopping cart | Spring Boot, Redis |
| Order Service | Handles order processing and tracking | Spring Boot, MongoDB |
| Payment Service | Manages payment processing | Spring Boot, PostgreSQL |
| Inventory Service | Manages product inventory | Spring Boot, MongoDB |
| File Manager | Handles file uploads and storage | Spring Boot, MinIO |
| Orchestrator | Coordinates workflows across services | Spring Boot, Kafka |
| Discovery Server | Service registration and discovery | Eureka |
| Gateway Server | API Gateway | Spring Cloud Gateway |

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.3
- Spring Cloud
- Spring Data JPA/MongoDB
- Keycloak
- Redis
- MongoDB
- PostgreSQL
- Kafka

### Frontend
- Angular
- Bootstrap
- HTML/CSS
- TypeScript
- RxJS
- NgRx (for state management)

### DevOps & Infrastructure
- Docker
- Kubernetes
- Helm
- Jenkins
- Grafana
- Prometheus
- Loki
- Tempo
- GitOps workflow

## Getting Started

### Prerequisites
- Git
- Docker
- Kubernetes
- Kubectl
- Helm
- JDK 17
- Node.js and npm
- Angular CLI

### Local Deployment

#### 1. Clone the repository
```bash
git clone https://github.com/pranayraut11/ecom.git
cd ecom
```

#### 2. Deploy using Helm
```bash
cd Deployment/helm/
helm install dev-env environment/dev
```

#### 3. Verify deployment
```bash
kubectl get pods
```

You should see output similar to:
```
NAME                                              READY   STATUS    RESTARTS   AGE
cart-deployment-b9b9b7487-b8cwq                   1/1     Running   0          2m33s
dev-env-keycloak-0                                1/1     Running   0          2m33s
dev-env-minio-554ff94df-d8f9t                     1/1     Running   0          2m33s
dev-env-mongodb-bc744d44d-w7nqq                   1/1     Running   0          2m33s
dev-env-postgresql-0                              1/1     Running   0          2m33s
dev-env-redis-master-0                            1/1     Running   0          2m33s
dev-env-redis-replicas-0                          1/1     Running   0          2m33s
ecom-ui-84465cd9cf-jlrv4                          1/1     Running   0          2m33s
filemanager-service-deployment-6c99b9f979-zgrw7   1/1     Running   0          2m33s
inventory-deployment-85989869cd-xtvxg             1/1     Running   0          2m33s
orchestrator-deployment-74d4659566-rd2sq          1/1     Running   0          2m33s
product-deployment-5c6f5dbbbf-jqlc5               1/1     Running   0          2m33s
user-service-deployment-7db7bd9496-v5xw5          1/1     Running   0          2m33s
```

### Frontend Development

Navigate to the UI application directory:
```bash
cd WebApps/ecom-ui
```

Install dependencies:
```bash
npm install
```

Start the development server:
```bash
ng serve
```

Access the application at: http://localhost:4200/

## Services Development

### Building Services
```bash
cd Services
mvn clean install
```

### Running Individual Services Locally
```bash
cd Services/<service-name>
mvn spring-boot:run
```

## Docker Images

Each service has its own Dockerfile for containerization. You can build individual service images or use the provided docker-compose files.

Example to build a service image:
```bash
cd Services/<service-name>
docker build -t ecom/<service-name>:latest .
```

## Deployment Options

### Kubernetes
Kubernetes deployment files are available in the `Deployment/k8` directory.

### Helm Charts
Helm charts for deployment are available in the `Deployment/helm` directory.

### Docker Compose
Various docker-compose files are available in the `Deployment/dockercompose` directory for different components:
- Kafka
- Keycloak
- MongoDB
- Services
- Observability stack (Grafana, Prometheus, Loki, Tempo)

## Monitoring and Observability

The application includes a comprehensive observability stack:
- **Metrics**: Prometheus and Grafana for monitoring system and application metrics
- **Logging**: Loki for log aggregation and visualization
- **Distributed Tracing**: Tempo for tracing requests across microservices

## API Documentation

API documentation is available via Swagger UI for each service at:
```
http://<service-host>:<service-port>/swagger-ui.html
```

## Security

- Keycloak for identity and access management
- OAuth2/OpenID Connect for authentication
- Role-based access control
- API Gateway for securing service endpoints

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

Project Link: [https://github.com/pranayraut11/ecom](https://github.com/pranayraut11/ecom)

## Getting Started

### Prerequisites
- Git
- Docker
- Kubernetes
- Kubectl
- Helm
- JDK 17
- Node.js and npm
- Angular CLI

### Local Deployment

#### 1. Clone the repository
```bash
git clone https://github.com/pranayraut11/ecom.git
cd ecom
```

#### 2. Deploy using Helm
```bash
cd Deployment/helm/
helm install dev-env environment/dev
```

#### 3. Verify deployment
```bash
kubectl get pods
```

### Frontend Development

Navigate to the UI application directory:
```bash
cd WebApps/ecom-ui
```

Install dependencies:
```bash
npm install
```

Start the development server:
```bash
ng serve
```

Access the application at: http://localhost:4200/

## Services Development

### Building Services
```bash
cd Services
mvn clean install
```

## Docker Images

Each service has its own Dockerfile for containerization. You can build individual service images or use the provided docker-compose files.

## Deployment Options

### Kubernetes
Kubernetes deployment files are available in the `Deployment/k8` directory.

### Helm Charts
Helm charts for deployment are available in the `Deployment/helm` directory.

### Docker Compose
Various docker-compose files are available in the `Deployment/dockercompose` directory for different components:
- Kafka
- Keycloak
- MongoDB
- Services
- Observability stack (Grafana, Prometheus, Loki, Tempo)

## Monitoring and Observability

The application includes a comprehensive observability stack:
- Metrics: Prometheus and Grafana
- Logging: Loki
- Distributed Tracing: Tempo

## Contributing

Please read the contributing guidelines before submitting pull requests.