package com.ecom.authprovider.service;

import com.ecom.authprovider.dto.request.ClientRequest;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.ClientManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {

    private final ClientManager clientManager;

    /**
     * Creates a new client in the specified realm.
     *
     * @param request the client creation request
     * @return true if the client was created successfully
     * @throws KeycloakServiceException if client creation fails
     * @throws IllegalArgumentException if the request parameters are invalid
     */
    public boolean createClient(ClientRequest request) {
        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("Client request cannot be null");
            }

            if (request.getClientId() == null || request.getClientId().trim().isEmpty()) {
                throw new IllegalArgumentException("Client ID cannot be empty");
            }

            if (request.getRedirectUri() == null || request.getRedirectUri().trim().isEmpty()) {
                throw new IllegalArgumentException("Redirect URI cannot be empty");
            }

            // For confidential clients, secret is required
            if (!request.isPublicClient() &&
                (request.getClientSecret() == null || request.getClientSecret().trim().isEmpty())) {
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
                        request.getRedirectUri());
            } else {
                created = clientManager.createConfidentialClient(
                        request.getClientId(),
                        request.getRedirectUri(),
                        request.getClientSecret());
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
