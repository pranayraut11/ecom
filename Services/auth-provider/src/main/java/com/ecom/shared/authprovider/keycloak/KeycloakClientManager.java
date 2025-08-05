package com.ecom.shared.authprovider.keycloak;

import com.ecom.shared.authprovider.keycloak.api.ClientManager;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of ClientManager for handling Keycloak client operations.
 */
@Slf4j
@RequiredArgsConstructor
public class KeycloakClientManager implements ClientManager {

    private static final int CREATED_STATUS = 201;
    private static final int BAD_REQUEST_STATUS = 400;
    private static final String ALREADY_EXISTS_ERROR = "already exists";
    private static final String CLIENT_SECRET_AUTHENTICATOR = "client-secret";

    private final RealmResource realmResource;
    private final Keycloak keycloak;  // Store the Keycloak client

    @PreDestroy
    public void cleanup() {
        // Close the Keycloak client when this bean is destroyed
        KeycloakAdminClientFactory.closeKeycloakClient(keycloak);
        log.debug("Closed Keycloak client in ClientManager");
    }

    /**
     * Creates a public client in the realm.
     *
     * @param clientId Client identifier
     * @param redirectUri Redirect URI for the client
     * @return true if client was created or already exists, false otherwise
     */
    public boolean createPublicClient(String clientId, String redirectUri) {
        return createClient(clientId, redirectUri, true, null);
    }

    /**
     * Creates a confidential client in the realm.
     *
     * @param clientId Client identifier
     * @param redirectUri Redirect URI for the client
     * @param clientSecret Secret for the confidential client
     * @return true if client was created or already exists, false otherwise
     */
    public boolean createConfidentialClient(String clientId, String redirectUri, String clientSecret) {
        return createClient(clientId, redirectUri, false, clientSecret);
    }

    /**
     * Creates a client with the specified configuration.
     *
     * @param clientId Client identifier
     * @param redirectUri Redirect URI for the client
     * @param isPublic Whether the client is public or confidential
     * @param clientSecret Secret for confidential clients (null for public clients)
     * @return true if client was created or already exists, false otherwise
     */
    private boolean createClient(String clientId, String redirectUri, boolean isPublic, String clientSecret) {
        log.info("Creating {} client '{}' with redirect URI: {}",
                isPublic ? "public" : "confidential", clientId, redirectUri);

        ClientsResource clientsResource = realmResource.clients();

        // Check if client already exists
        List<ClientRepresentation> existingClients = clientsResource.findByClientId(clientId);
        if (!existingClients.isEmpty()) {
            log.info("Client '{}' already exists", clientId);
            return true;
        }

        // Create client representation
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(clientId);
        client.setEnabled(true);
        client.setDirectAccessGrantsEnabled(true);

        // Configure redirect URIs
        if (redirectUri != null && !redirectUri.isEmpty()) {
            client.setRedirectUris(Collections.singletonList(redirectUri));
            client.setWebOrigins(Collections.singletonList("*"));
        }

        // Set public/confidential properties
        if (isPublic) {
            client.setPublicClient(true);
            client.setBearerOnly(false);
        } else {
            client.setPublicClient(false);
            client.setBearerOnly(false);
            client.setClientAuthenticatorType(CLIENT_SECRET_AUTHENTICATOR);
            client.setSecret(clientSecret);
        }

        try (Response response = clientsResource.create(client)) {
            if (response.getStatus() == CREATED_STATUS) {
                log.info("Successfully created {} client '{}'",
                        isPublic ? "public" : "confidential", clientId);
                return true;
            } else {
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create client: Status {}, Details: {}",
                        response.getStatus(), errorBody);
                return false;
            }
        } catch (WebApplicationException e) {
            try (Response response = e.getResponse()) {
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create client: Status {}, Details: {}",
                        response.getStatus(), errorBody);

                // If client already exists (race condition), consider it a success
                if (response.getStatus() == BAD_REQUEST_STATUS &&
                        errorBody.contains(ALREADY_EXISTS_ERROR)) {
                    log.info("Client '{}' already exists (concurrent creation)", clientId);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Unexpected error creating client '{}'", clientId, e);
            return false;
        }
    }
}
