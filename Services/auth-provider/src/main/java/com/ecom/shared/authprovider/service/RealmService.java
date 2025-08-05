package com.ecom.shared.authprovider.service;

import com.ecom.shared.authprovider.dto.request.RealmRequest;
import com.ecom.shared.authprovider.keycloak.KeycloakAdminClientFactory;
import com.ecom.shared.authprovider.keycloak.api.RealmManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealmService {

    private final RealmManager realmManager;

    /**
     * Creates a new realm in Keycloak based on the provided request.
     *
     * @param request the realm creation request
     * @return true if the realm was created successfully or already exists, false otherwise
     */
    public boolean createRealm(RealmRequest request) {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakAdminClientFactory.getKeycloakClient();

            String realmName = request.getName();
            if (request.getDisplayName() == null || request.getDisplayName().isEmpty()) {
                request.setDisplayName(realmName);
            }

            log.info("Creating realm with name: {}", realmName);
            return realmManager.createRealm(realmName);
        } catch (Exception e) {
            log.error("Error creating realm: {}", e.getMessage(), e);
            return false;
        } finally {
            KeycloakAdminClientFactory.closeKeycloakClient(keycloak);
        }
    }
}
