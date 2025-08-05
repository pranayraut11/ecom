package com.ecom.shared.authprovider.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import jakarta.ws.rs.core.Response;

/**
 * Factory class to create and manage Keycloak admin client instances.
 */
@Slf4j
public class KeycloakAdminClientFactory {

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String MASTER_REALM = "master";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String ADMIN_CLIENT = "admin-cli";

    /**
     * Creates and returns a Keycloak admin client.
     *
     * @return Keycloak admin client instance
     */
    public static Keycloak getKeycloakClient() {
        log.info("Creating Keycloak admin client for server: {}", SERVER_URL);
        return KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(MASTER_REALM)
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .clientId(ADMIN_CLIENT)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    /**
     * Extracts the entity ID from a response's location header.
     *
     * @param response The response containing the location header
     * @return The extracted entity ID
     */
    public static String getCreatedId(Response response) {
        String location = response.getHeaderString("Location");
        if (location == null) {
            return null;
        }
        return location.replaceAll(".*/([^/]+)$", "$1");
    }

    /**
     * Closes the Keycloak client if it's closeable.
     *
     * @param keycloak The Keycloak client to close
     */
    public static void closeKeycloakClient(Keycloak keycloak) {
        if (keycloak != null && keycloak instanceof AutoCloseable) {
            try {
                ((AutoCloseable) keycloak).close();
                log.debug("Keycloak client closed successfully");
            } catch (Exception e) {
                log.warn("Error closing Keycloak client", e);
            }
        }
    }
}
