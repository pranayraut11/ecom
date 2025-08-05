package com.ecom.shared.authprovider.service;

import com.ecom.shared.authprovider.config.KeycloakManagerConfig;
import com.ecom.shared.authprovider.dto.request.RoleRequest;
import com.ecom.shared.authprovider.keycloak.api.RoleManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    private final KeycloakManagerConfig.RoleManagerFactory roleManagerFactory;

    /**
     * Creates a new role in the specified realm.
     *
     * @param realmName the name of the realm
     * @param request the role creation request
     * @return true if the role was created successfully or already exists, false otherwise
     */
    public boolean createRole(String realmName, RoleRequest request) {
        try {
            // Get a role manager specifically for the requested realm
            RoleManager roleManager = roleManagerFactory.getManager(realmName);

            log.info("Creating role '{}' in realm '{}'", request.getName(), realmName);
            return roleManager.createRealmRole(request.getName());
        } catch (Exception e) {
            log.error("Error creating role in realm {}: {}", realmName, e.getMessage(), e);
            return false;
        }
    }
}
