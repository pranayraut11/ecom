FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the jar file
COPY target/orchestrator-service-1.0.0.jar app.jar

# Create non-root user
RUN addgroup --system orchestrator && adduser --system --group orchestrator
RUN chown -R orchestrator:orchestrator /app
USER orchestrator

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/api/orchestration/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
