package com.ecom.authprovider.service;

import com.ecom.authprovider.dto.request.RoleRequest;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.RoleManager;
import com.ecom.shared.common.config.common.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {


    private final RoleManager roleManager;

    /**
     * Creates a new role in the specified realm.
     *
     * @param request the role creation request
     * @return true if the role was created successfully
     * @throws KeycloakServiceException if role creation fails
     * @throws IllegalArgumentException if the request parameters are invalid
     */
    public boolean createRole(RoleRequest request) {
        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("Role request cannot be null");
            }

            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Role name cannot be empty");
            }

            // Get a role manager specifically for the requested realm

            log.info("Creating role '{}'", request.getName());
            boolean created = roleManager.createRealmRole(request.getName(), TenantContext.getTenantId());

            if (created) {
                log.info("Role '{}' created successfully", request.getName());
                return true;
            } else {
                String errorMessage = String.format("Failed to create role '%s'", request.getName());
                log.error(errorMessage);
                throw new KeycloakServiceException(errorMessage);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid role request: {}", e.getMessage());
            throw e;
        } catch (KeycloakServiceException e) {
            // Already formatted exception, just rethrow
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error creating role '%s': %s",
                request.getName(), e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
    }
}
