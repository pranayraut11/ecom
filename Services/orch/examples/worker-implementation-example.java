# ============================================================================
# Worker Implementation Example - Java Spring Boot
# ============================================================================
# This example shows how to implement a worker service that handles
# both DO and UNDO operations for orchestration steps
# ============================================================================

package com.example.tenant.worker;

import com.ecom.orchestrator.dto.ExecutionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Worker service for Tenant Creation orchestration
 * Implements both DO and UNDO operations for each step
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TenantCreationWorker {

    private final KafkaTemplate<String, ExecutionMessage> kafkaTemplate;
    private final RealmService realmService;
    private final ClientService clientService;

    // Store state for UNDO operations (in production, use Redis or Database)
    private final Map<String, String> undoStateStore = new ConcurrentHashMap<>();

    // ========================================================================
    // CREATE REALM - DO Operation
    // ========================================================================
    @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.do")
    public void handleCreateRealmDo(ExecutionMessage message) {
        String flowId = getHeader(message, "flowId");
        String stepName = getHeader(message, "stepName");

        log.info("Received DO request: flowId={}, stepName={}", flowId, stepName);

        try {
            // Extract payload
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String tenantName = (String) payload.get("tenantName");

            // Execute forward operation
            String realmId = realmService.createRealm(tenantName);
            log.info("Created realm: realmId={}, flowId={}", realmId, flowId);

            // Store state for potential UNDO
            storeUndoState(flowId, "createRealm", realmId);

            // Send SUCCESS response
            sendResponse(message, "DO", true, null);

        } catch (Exception e) {
            log.error("Failed to create realm: flowId={}", flowId, e);
            // Send FAILURE response
            sendResponse(message, "DO", false, e.getMessage());
        }
    }

    // ========================================================================
    // CREATE REALM - UNDO Operation
    // ========================================================================
    @KafkaListener(topics = "orchestrator.tenantCreation.createRealm.undo")
    public void handleCreateRealmUndo(ExecutionMessage message) {
        String flowId = getHeader(message, "flowId");
        String stepName = getHeader(message, "stepName");

        log.info("Received UNDO request: flowId={}, stepName={}", flowId, stepName);

        try {
            // Retrieve stored state
            String realmId = getUndoState(flowId, "createRealm");

            if (realmId == null) {
                log.warn("No realm to undo for flowId={}, treating as success", flowId);
                sendResponse(message, "UNDO", true, null);
                return;
            }

            // Execute rollback operation
            realmService.deleteRealm(realmId);
            log.info("Deleted realm: realmId={}, flowId={}", realmId, flowId);

            // Clear undo state
            clearUndoState(flowId, "createRealm");

            // Send SUCCESS response
            sendResponse(message, "UNDO", true, null);

        } catch (Exception e) {
            // Handle "already deleted" as success (idempotency)
            if (e.getMessage().contains("not found") || e.getMessage().contains("does not exist")) {
                log.warn("Realm already deleted, treating as success: flowId={}", flowId);
                clearUndoState(flowId, "createRealm");
                sendResponse(message, "UNDO", true, null);
            } else {
                log.error("Failed to undo realm creation: flowId={}", flowId, e);
                sendResponse(message, "UNDO", false, e.getMessage());
            }
        }
    }

    // ========================================================================
    // CREATE CLIENT - DO Operation
    // ========================================================================
    @KafkaListener(topics = "orchestrator.tenantCreation.createClient.do")
    public void handleCreateClientDo(ExecutionMessage message) {
        String flowId = getHeader(message, "flowId");
        String stepName = getHeader(message, "stepName");

        log.info("Received DO request: flowId={}, stepName={}", flowId, stepName);

        try {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            String clientName = (String) payload.get("clientName");
            String realmId = (String) payload.get("realmId");

            // Execute forward operation
            String clientId = clientService.createClient(realmId, clientName);
            log.info("Created client: clientId={}, flowId={}", clientId, flowId);

            // Store state for potential UNDO
            storeUndoState(flowId, "createClient", clientId);

            // Send SUCCESS response
            sendResponse(message, "DO", true, null);

        } catch (Exception e) {
            log.error("Failed to create client: flowId={}", flowId, e);
            sendResponse(message, "DO", false, e.getMessage());
        }
    }

    // ========================================================================
    // CREATE CLIENT - UNDO Operation
    // ========================================================================
    @KafkaListener(topics = "orchestrator.tenantCreation.createClient.undo")
    public void handleCreateClientUndo(ExecutionMessage message) {
        String flowId = getHeader(message, "flowId");
        String stepName = getHeader(message, "stepName");

        log.info("Received UNDO request: flowId={}, stepName={}", flowId, stepName);

        try {
            String clientId = getUndoState(flowId, "createClient");

            if (clientId == null) {
                log.warn("No client to undo for flowId={}, treating as success", flowId);
                sendResponse(message, "UNDO", true, null);
                return;
            }

            // Execute rollback operation
            clientService.deleteClient(clientId);
            log.info("Deleted client: clientId={}, flowId={}", clientId, flowId);

            clearUndoState(flowId, "createClient");
            sendResponse(message, "UNDO", true, null);

        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Client already deleted, treating as success: flowId={}", flowId);
                clearUndoState(flowId, "createClient");
                sendResponse(message, "UNDO", true, null);
            } else {
                log.error("Failed to undo client creation: flowId={}", flowId, e);
                sendResponse(message, "UNDO", false, e.getMessage());
            }
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Send response back to orchestrator
     */
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

        log.info("Sent {} response: flowId={}, success={}",
                action, headers.get("flowId"), success);
    }

    /**
     * Get header value safely
     */
    private String getHeader(ExecutionMessage message, String key) {
        Object value = message.getHeaders().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Store state for UNDO operation
     * In production, use Redis or Database with TTL
     */
    private void storeUndoState(String flowId, String stepName, String state) {
        String key = flowId + ":" + stepName;
        undoStateStore.put(key, state);
        log.debug("Stored undo state: key={}, value={}", key, state);
    }

    /**
     * Retrieve state for UNDO operation
     */
    private String getUndoState(String flowId, String stepName) {
        String key = flowId + ":" + stepName;
        return undoStateStore.get(key);
    }

    /**
     * Clear undo state after successful UNDO
     */
    private void clearUndoState(String flowId, String stepName) {
        String key = flowId + ":" + stepName;
        undoStateStore.remove(key);
        log.debug("Cleared undo state: key={}", key);
    }
}

// ============================================================================
// Key Implementation Points:
// ============================================================================
// 1. ✅ Listen to both DO and UNDO topics for each step
// 2. ✅ Store state needed for UNDO (use flowId as correlation ID)
// 3. ✅ Handle idempotency (DO: "already exists", UNDO: "not found")
// 4. ✅ Send proper response with action field (DO/UNDO)
// 5. ✅ Include status (true/false) and errorMessage in response
// 6. ✅ Log all operations with flowId for traceability
// 7. ✅ Handle exceptions gracefully
// 8. ✅ Clear state after successful UNDO
// ============================================================================

// ============================================================================
// Production Considerations:
// ============================================================================
// 1. Use Redis or Database for undo state (not in-memory Map)
// 2. Set TTL for undo state (e.g., 24 hours)
// 3. Add retry mechanism for external service calls
// 4. Implement circuit breakers for resilience
// 5. Add metrics and monitoring
// 6. Use distributed tracing (e.g., Sleuth, Zipkin)
// 7. Add unit and integration tests
// 8. Handle timeout scenarios
// ============================================================================

