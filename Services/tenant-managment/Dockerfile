# Multi-stage Docker build for Tenant Management Service
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --ingroup appgroup --no-create-home appuser

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the built jar from builder stage - use the actual jar name
COPY --from=builder /app/target/tenant-management-*.jar tenant-management.jar

# Change ownership to non-root user
RUN chown appuser:appgroup tenant-management.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization options
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application with the correct jar name
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/tenant-management.jar"]
