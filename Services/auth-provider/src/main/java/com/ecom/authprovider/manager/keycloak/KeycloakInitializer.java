package com.ecom.authprovider.manager.keycloak;

import com.ecom.authprovider.manager.api.ClientManager;
import com.ecom.authprovider.manager.api.RealmManager;
import com.ecom.authprovider.manager.api.RoleManager;
import com.ecom.authprovider.manager.api.UserManager;
import com.ecom.authprovider.util.KeycloakUtil;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Component that initializes Keycloak with a realm, clients, roles, and users.
 * This runs automatically when the Spring application is ready.
 */
@Component
@Slf4j
public class KeycloakInitializer {

    private static final String REALM_NAME = "ecom-dev";
    private static final String FRONTEND_CLIENT_ID = "frontend-app";
    private static final String BACKEND_CLIENT_ID = "backend-service";
    private static final String FRONTEND_REDIRECT_URI = "https://frontend.example.com/*";
    private static final String BACKEND_SECRET = "backend-secret";
    private static final String USER_ROLE = "user";
    private static final String ADMIN_ROLE = "admin";
    KeycloakUtil keycloakAdminConfig;
    KeycloakInitializer( KeycloakUtil keycloakAdminConfig) {
        this.keycloakAdminConfig = keycloakAdminConfig;
    }

    /**
     * Initializes Keycloak when the application has started.
     * This method is automatically called after the application is fully started.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application started, initializing Keycloak configuration...");
        //initializeKeycloak();
    }

    /**
     * Initializes a Keycloak realm with clients, roles, and a user.
     */
    public void initializeKeycloak() {
        Keycloak keycloak = null;

        try {
            // Get Keycloak admin client
            keycloak = keycloakAdminConfig.createAdminClient();

            // Create realm if it doesn't exist
            RealmManager realmManager = new KeycloakRealmManager(keycloakAdminConfig);
            boolean realmCreated = realmManager.createRealm(REALM_NAME);

            if (!realmCreated) {
                log.error("Failed to create realm '{}', initialization aborted", REALM_NAME);
                return;
            }

            // Get realm resource for further operations
            RealmResource realmResource = keycloak.realm(REALM_NAME);

            // Create clients
            ClientManager clientManager = null;//new KeycloakClientManager(realmResource,keycloak);
            boolean frontendClientCreated = clientManager.createPublicClient(
                    FRONTEND_CLIENT_ID, FRONTEND_REDIRECT_URI);
            boolean backendClientCreated = clientManager.createConfidentialClient(
                    BACKEND_CLIENT_ID, null, BACKEND_SECRET);

            if (!frontendClientCreated || !backendClientCreated) {
                log.error("Failed to create one or more clients, but will continue with initialization");
            }

            // Create roles
            RoleManager roleManager = new KeycloakRoleManager(keycloakAdminConfig);
            boolean userRoleCreated =false;//roleManager.createRealmRole(USER_ROLE);
            boolean adminRoleCreated= false;//roleManager.createRealmRole(ADMIN_ROLE);

            if (!userRoleCreated || !adminRoleCreated) {
                log.error("Failed to create one or more roles, but will continue with initialization");
            }

            // Create admin user with roles
            UserManager userManager = new KeycloakUserManager( keycloakAdminConfig);
            String userCreated = null;

            if (null == userCreated || userCreated.isEmpty()) {
                log.error("Failed to create admin user");
            }

            log.info("Keycloak initialization completed successfully");

        } catch (Exception e) {
            log.error("Error during Keycloak initialization", e);
        } finally {
            // Close Keycloak client
        }
    }

}
