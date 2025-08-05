package com.ecom.shared.authprovider.service;

import com.ecom.shared.authprovider.config.KeycloakManagerConfig;
import com.ecom.shared.authprovider.dto.request.UserRequest;
import com.ecom.shared.authprovider.keycloak.api.RoleManager;
import com.ecom.shared.authprovider.keycloak.api.UserManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final KeycloakManagerConfig.RoleManagerFactory roleManagerFactory;
    private final KeycloakManagerConfig.UserManagerFactory userManagerFactory;

    /**
     * Creates a new user in the specified realm.
     *
     * @param realmName the name of the realm
     * @param request the user creation request
     * @return the ID of the created user, or null if creation failed
     */
    public String createUser(String realmName, UserRequest request) {
        try {
            // Prepare roles - if no roles provided, assign default USER role
            List<String> roles = request.getRoles();
            if (roles == null || roles.isEmpty()) {
                roles = new ArrayList<>();
                roles.add("user");
                log.info("No roles specified for user '{}', assigning default role 'USER'", request.getUsername());
            }

            // Get managers specifically for the requested realm
            RoleManager roleManager = roleManagerFactory.getManager(realmName);
            UserManager userManager = userManagerFactory.getManager(realmName, roleManager);

            log.info("Creating user '{}' in realm '{}'", request.getUsername(), realmName);
            String userId = userManager.createUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    roles
            );

            if (userId != null) {
                log.info("User '{}' created with ID: {}", request.getUsername(), userId);
            } else {
                log.error("Failed to create user '{}'", request.getUsername());
            }

            return userId;
        } catch (Exception e) {
            log.error("Error creating user in realm {}: {}", realmName, e.getMessage(), e);
            return null;
        }
    }
}
