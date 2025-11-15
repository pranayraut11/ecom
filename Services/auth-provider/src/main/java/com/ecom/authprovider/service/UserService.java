package com.ecom.authprovider.service;

import com.ecom.authprovider.dto.request.UserRequest;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.keycloak.KeycloakUserManager;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final KeycloakUserManager userManager;
    private final ObjectMapper objectMapper;
    private final OrchestrationService orchestrationService;

    public boolean createDefaultUserByEvent(ExecutionMessage executionMessage) {
        log.info("Creating default user for tenant '{}'", executionMessage.getPayload());
        try {
            Map<String,Object> payload =  objectMapper.convertValue(executionMessage.getPayload(), Map.class);
            String realmName = payload.get("tenantName").toString();
            String email = payload.get("contactEmail").toString();
            UserRequest userRequest = UserRequest.builder()
                    .username(realmName).realmName(realmName)
                    .password(realmName).email(email)
                    .build();
            createUser(userRequest);
            log.info("Default user created successfully for tenant '{}'", executionMessage.getPayload());
        } catch (Exception e) {
            String errorMessage = String.format("Failed to create default user for tenant : %s",
                   e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
        orchestrationService.doNext(executionMessage);
        return true;
    }

    public boolean undoCreateDefaultUserByEvent(ExecutionMessage executionMessage) {
        log.info("Undoing default user creation for tenant '{}'", executionMessage.getPayload());
        try {
            String username = "defaultUser";
            Map payload = objectMapper.convertValue(executionMessage.getPayload(),Map.class);
            userManager.deleteUserByUsername(username, payload.get("tenantName").toString());
            log.info("Default user deletion successful for tenant '{}'", executionMessage.getPayload());
        } catch (Exception e) {
            String errorMessage = String.format("Failed to delete default user for tenant : %s",
                    e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
        orchestrationService.undoNext(executionMessage);
        return true;
    }


    /**
     * Creates a new user in the specified realm.
     *
     * @param request the user creation request
     * @return the ID of the created user
     * @throws KeycloakServiceException if user creation fails
     */
    public String createUser(UserRequest request) {
        try {
             // Prepare roles - if no roles provided, assign default USER role
            List<String> roles = request.getRoles();
            if (roles == null || roles.isEmpty()) {
                roles = new ArrayList<>();
                roles.add("user");
                log.info("No roles specified for user '{}', assigning default role 'USER'", request.getUsername());
            }

            // Get managers specifically for the requested realm

            log.info("Creating user '{}' in realm", request.getUsername());
            String userId = userManager.createUser(
                    request
            );

            if (userId != null) {
                log.info("User '{}' created with ID: {}", request.getUsername(), userId);
                return userId;
            } else {
                String errorMessage = String.format("Failed to create user '%s' - no user ID returned from Keycloak",
                    request.getUsername());
                log.error(errorMessage);
                throw new KeycloakServiceException(errorMessage);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid user request: {}", e.getMessage());
            throw e;
        } catch (KeycloakServiceException e) {
            // Already formatted exception, just rethrow
            throw e;
        } catch (NotFoundException e){
            String errorMessage = String.format("Realm not found for user '%s'",
                request.getUsername());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
         catch (Exception e) {
            String errorMessage = String.format("Error creating user '%s': %s",
                request.getUsername(), e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
    }
}
