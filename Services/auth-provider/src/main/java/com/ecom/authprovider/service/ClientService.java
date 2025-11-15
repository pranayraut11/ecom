package com.ecom.authprovider.service;

import com.ecom.authprovider.dto.request.ClientRequest;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.ClientManager;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {

    private final ClientManager clientManager;
    private final ObjectMapper objectMapper;
    private final OrchestrationService orchestrationService;

    public void createClientByEvent(ExecutionMessage executionMessage) {
            log.info("Received event to create client: {}", executionMessage);
            Map payloadMap = objectMapper.convertValue(executionMessage.getPayload(), Map.class);
            // Extract required fields for ClientRequest and realm
            String clientId = (String) payloadMap.get("tenantName");
            String redirectUri = (String) payloadMap.get("domain");
            ClientRequest clientRequest = ClientRequest.builder()
                    .clientId(clientId)
                    .redirectUri(redirectUri)
                    .clientSecret(UUID.randomUUID().toString())
                    .publicClient(false)
                    .build();
            boolean created = createClient(clientRequest, clientId);
            log.info("Client creation by event result: {}", created);
            orchestrationService.doNext(executionMessage);
    }


    public void undoCreateClientByEvent(ExecutionMessage executionMessage) {
            log.info("Received event to undo create client: {}", executionMessage);
            try {
                Map payloadMap = objectMapper.convertValue(executionMessage.getPayload(), Map.class);
                String clientId = (String) payloadMap.get("tenantName");
                boolean deleted = clientManager.deleteClient(clientId);
                log.info("Client deletion by event result: {}", deleted);
            } catch (Exception e) {
                log.error("Failed to delete client from event: {}", e.getMessage(), e);
                throw new KeycloakServiceException("Failed to delete client from event", e);
            }
           orchestrationService.undoNext(executionMessage);
    }
    /**
     * Creates a new client in the specified realm.
     *
     * @param request the client creation request
     * @return true if the client was created successfully
     * @throws KeycloakServiceException if client creation fails
     * @throws IllegalArgumentException if the request parameters are invalid
     */
    public boolean createClient(ClientRequest request,String realm) {
        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("Client request cannot be null");
            }

            if (request.getClientId() == null || request.getClientId().trim().isEmpty()) {
                throw new IllegalArgumentException("Client ID cannot be empty");
            }

            // For confidential clients, secret is required
            if (!request.isPublicClient() && (request.getClientSecret() == null || request.getClientSecret().trim().isEmpty())) {
                throw new IllegalArgumentException("Client secret is required for confidential clients");
            }

            // Get a client manager specifically for the requested realm

            log.info("Creating client '{}' of type {}",
                request.getClientId(),
                request.isPublicClient() ? "public" : "confidential");

            boolean created;
            if (request.isPublicClient()) {
                created = clientManager.createPublicClient(
                        request.getClientId(),
                        request.getRedirectUri(),realm);
            } else {
                created = clientManager.createConfidentialClient(
                        request.getClientId(),
                        request.getRedirectUri(),
                        request.getClientSecret(),realm);
            }

            if (created) {
                log.info("Client '{}' created successfully", request.getClientId());
                return true;
            } else {
                String errorMessage = String.format("Failed to create client '%s'", request.getClientId());
                log.error(errorMessage);
                throw new KeycloakServiceException(errorMessage);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid client request: {}", e.getMessage());
            throw e;
        } catch (KeycloakServiceException e) {
            // Already formatted exception, just rethrow
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error creating client '%s': %s",
                request.getClientId(), e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
    }
}
