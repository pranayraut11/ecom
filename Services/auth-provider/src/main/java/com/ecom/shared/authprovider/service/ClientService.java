package com.ecom.shared.authprovider.service;

import com.ecom.shared.authprovider.config.KeycloakManagerConfig;
import com.ecom.shared.authprovider.dto.request.ClientRequest;
import com.ecom.shared.authprovider.keycloak.api.ClientManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {

    private final KeycloakManagerConfig.ClientManagerFactory clientManagerFactory;

    /**
     * Creates a new client in the specified realm.
     *
     * @param realmName the name of the realm
     * @param request the client creation request
     * @return true if the client was created successfully or already exists, false otherwise
     */
    public boolean createClient(String realmName, ClientRequest request) {
        try {
            // Get a client manager specifically for the requested realm
            ClientManager clientManager = clientManagerFactory.getManager(realmName);

            log.info("Creating client '{}' in realm '{}'", request.getClientId(), realmName);

            if (request.isPublicClient()) {
                return clientManager.createPublicClient(
                        request.getClientId(),
                        request.getRedirectUri());
            } else {
                return clientManager.createConfidentialClient(
                        request.getClientId(),
                        request.getRedirectUri(),
                        request.getClientSecret());
            }
        } catch (Exception e) {
            log.error("Error creating client in realm {}: {}", realmName, e.getMessage(), e);
            return false;
        }
    }
}
