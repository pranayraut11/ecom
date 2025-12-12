package com.ecom.orchestrator.client.example;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Example Handler Service Implementation
 *
 * This demonstrates how to implement handler methods for orchestration steps
 *
 * Key Points:
 * 1. Methods must accept ExecutionMessage parameter
 * 2. Extract payload and headers from ExecutionMessage
 * 3. Perform business logic
 * 4. Throw exception for retriable errors
 * 5. System automatically calls doNext() on success
 * 6. System automatically calls undoNext() and retries on failure
 */
@Slf4j
@Service("exampleRealmService")  // Bean name must match handlerClass in YAML
public class ExampleRealmServiceHandler {

    /**
     * DO Method - Creates a realm
     *
     * This method is registered in orchconfig.yml as:
     * doMethod: createRealmByEvent
     *
     * Flow:
     * 1. Message arrives on topic: orchestrator.tenantCreation.createRealm.do
     * 2. This method executes
     * 3. On success: System automatically calls orchestrationService.doNext()
     * 4. On failure: System retries, then triggers undo workflow
     */
    public void createRealmByEvent(ExecutionMessage message) {
        try {
            log.info("🔧 Executing createRealmByEvent");
            log.info("FlowId: {}", message.getHeaders().get("flowId"));
            log.info("Current Step: {}", message.getHeaders().get("currentStep"));

            // Extract payload
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String tenantName = (String) payload.get("tenantName");
            String realmName = (String) payload.get("schemaName");
            String domain = (String) payload.get("domain");

            log.info("Creating realm: {} for tenant: {}", realmName, tenantName);

            // Perform actual business logic
            createRealm(realmName, domain);

            // Store realm ID in payload for next steps
            payload.put("realmId", "realm-" + System.currentTimeMillis());

            log.info("✅ Realm created successfully: {}", realmName);

            // No need to call orchestrationService.doNext() - it's automatic!
            // The AdvancedDynamicWorkerRegistrar will call it for you

        } catch (Exception e) {
            log.error("❌ Failed to create realm", e);
            // Throwing exception will trigger retry mechanism
            // After max retries, system will automatically:
            // 1. Send message to DLQ
            // 2. Trigger undo workflow
            throw new RuntimeException("Realm creation failed: " + e.getMessage(), e);
        }
    }

    /**
     * UNDO Method - Deletes a realm (compensating transaction)
     *
     * This method is registered in orchconfig.yml as:
     * undoMethod: undoCreateRealmByEvent
     *
     * Flow:
     * 1. Message arrives on topic: orchestrator.tenantCreation.createRealm.undo
     * 2. This method executes
     * 3. On success: System automatically calls orchestrationService.undoNext()
     *    to undo the previous step
     */
    public void undoCreateRealmByEvent(ExecutionMessage message) {
        try {
            log.warn("↩️ Executing undoCreateRealmByEvent");
            log.info("FlowId: {}", message.getHeaders().get("flowId"));

            // Extract payload
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String realmId = (String) payload.get("realmId");
            String realmName = (String) payload.get("schemaName");

            if (realmId == null) {
                log.warn("⚠️ No realmId found, realm might not have been created");
                return;
            }

            log.info("Deleting realm: {} (ID: {})", realmName, realmId);

            // Perform compensating transaction
            deleteRealm(realmId);

            // Remove realm ID from payload
            payload.remove("realmId");

            log.info("✅ Realm deleted successfully (compensated): {}", realmName);

            // No need to call orchestrationService.undoNext() - it's automatic!

        } catch (Exception e) {
            log.error("❌ Failed to undo realm creation", e);
            // Undo operations should be more lenient
            // If resource doesn't exist, consider it already deleted
            if (e.getMessage().contains("not found")) {
                log.warn("⚠️ Realm already deleted or never existed");
            } else {
                throw new RuntimeException("Realm undo failed: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Example with validation and retriable vs non-retriable errors
     */
    public void createClientByEvent(ExecutionMessage message) {
        try {
            log.info("🔧 Executing createClientByEvent");

            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String realmId = (String) payload.get("realmId");

            // Validation - non-retriable error
            if (realmId == null) {
                log.error("❌ RealmId is required but not found in payload");
                // This is a non-retriable error - don't retry
                throw new IllegalArgumentException("RealmId is required");
            }

            String clientName = (String) payload.get("tenantName") + "-client";

            // Simulate retriable error (e.g., database temporarily unavailable)
            if (Math.random() < 0.3) {  // 30% chance to simulate error
                throw new RuntimeException("Database connection timeout - retriable");
            }

            // Create client
            createClient(realmId, clientName);
            payload.put("clientId", "client-" + System.currentTimeMillis());

            log.info("✅ Client created successfully: {}", clientName);

        } catch (IllegalArgumentException e) {
            // Non-retriable validation errors
            log.error("❌ Validation error: {}", e.getMessage());
            // Send directly to DLQ without retry
            throw e;
        } catch (Exception e) {
            // Retriable errors
            log.error("❌ Retriable error creating client", e);
            throw new RuntimeException("Client creation failed (retriable): " + e.getMessage(), e);
        }
    }

    public void undoCreateClientByEvent(ExecutionMessage message) {
        try {
            log.warn("↩️ Executing undoCreateClientByEvent");

            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String clientId = (String) payload.get("clientId");

            if (clientId != null) {
                deleteClient(clientId);
                payload.remove("clientId");
                log.info("✅ Client deleted successfully (compensated)");
            } else {
                log.warn("⚠️ No clientId found, client might not have been created");
            }

        } catch (Exception e) {
            log.error("❌ Failed to undo client creation", e);
            // Undo failures are logged but don't stop the undo chain
        }
    }

    // ==================== Business Logic Methods ====================
    // These would call actual services, repositories, external APIs, etc.

    private void createRealm(String realmName, String domain) {
        // Simulate realm creation
        log.debug("Creating realm in identity provider: {}", realmName);
        // Call to Keycloak, Auth0, or other identity provider
        simulateExternalApiCall();
    }

    private void deleteRealm(String realmId) {
        // Simulate realm deletion
        log.debug("Deleting realm from identity provider: {}", realmId);
        simulateExternalApiCall();
    }

    private void createClient(String realmId, String clientName) {
        // Simulate client creation
        log.debug("Creating client in realm {}: {}", realmId, clientName);
        simulateExternalApiCall();
    }

    private void deleteClient(String clientId) {
        // Simulate client deletion
        log.debug("Deleting client: {}", clientId);
        simulateExternalApiCall();
    }

    private void simulateExternalApiCall() {
        // Simulate network delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

