package com.ecom.authprovider.service;

import com.ecom.authprovider.dto.request.UserRequest;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.RoleManager;
import com.ecom.authprovider.manager.api.UserManager;
import com.ecom.authprovider.manager.keycloak.KeycloakUserManager;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final KeycloakUserManager userManager;

    /**
     * Creates a new user in the specified realm.
     *
     * @param request the user creation request
     * @return the ID of the created user
     * @throws KeycloakServiceException if user creation fails
     */
    public String createUser(UserRequest request) {
        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("User request cannot be null");
            }

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

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
