package com.ecom.authprovider.service;

import com.ecom.authprovider.dto.request.RoleRequest;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.RoleManager;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import com.ecom.shared.common.config.common.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {


    private final RoleManager roleManager;
    private final OrchestrationService orchestrationService;
    private final ObjectMapper objectMapper;

    public boolean setupDefaultRolesByEvent(ExecutionMessage executionMessage) {
        Map<String,Object> payload =  objectMapper.convertValue(executionMessage.getPayload(), Map.class);
        String realmName = payload.get("tenantName").toString();

        log.info("Setting up default roles for tenant '{}'", realmName);
        try {
            String[] defaultRoles = {"user", "admin", "manager"};
            for (String roleName : defaultRoles) {
                RoleRequest roleRequest = RoleRequest.builder()
                        .name(roleName).realmName(realmName)
                        .build();
                createRole(roleRequest);
            }
            log.info("Default roles set up successfully for tenant '{}'", realmName);

        } catch (Exception e) {
            String errorMessage = String.format("Failed to set up default roles for tenant '%s': %s",
                    realmName, e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
        orchestrationService.doNext(executionMessage);
        return true;
    }

    public boolean undoSetupDefaultRolesByEvent(ExecutionMessage executionMessage){
        log.info("Undoing default roles setup for tenant '{}'", TenantContext.getTenantId());
        try {
            String[] defaultRoles = {"user", "admin", "manager"};
            for (String roleName : defaultRoles) {
                roleManager.deleteRealmRole(roleName, TenantContext.getTenantId());
            }
            log.info("Default roles deletion successful for tenant '{}'", TenantContext.getTenantId());

        } catch (Exception e) {
            String errorMessage = String.format("Failed to delete default roles for tenant '%s': %s",
                    TenantContext.getTenantId(), e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
        orchestrationService.undoNext(executionMessage);
        return true;
    }

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

            // Get a role manager specifically for the requested realm

            log.info("Creating role '{}'", request.getName());
            boolean created = roleManager.createRealmRole(request.getName(), request.getRealmName());

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
