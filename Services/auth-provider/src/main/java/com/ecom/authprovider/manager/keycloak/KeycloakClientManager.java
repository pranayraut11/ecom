package com.ecom.authprovider.manager.keycloak;

import com.ecom.authprovider.dto.request.ClientRequest;
import com.ecom.authprovider.manager.api.ClientManager;
import com.ecom.authprovider.util.KeycloakUtil;
import com.ecom.shared.common.config.common.TenantContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Implementation of ClientManager for handling Keycloak client operations.
 * This class provides methods to create, retrieve, update, and delete Keycloak clients.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakClientManager implements ClientManager {

    // Constants
    private static final int CREATED_STATUS = 201;
    private static final int BAD_REQUEST_STATUS = 400;
    private static final String ALREADY_EXISTS_ERROR = "already exists";
    private static final String CLIENT_SECRET_AUTHENTICATOR = "client-secret";

    // Default configuration values
    private static final List<String> DEFAULT_WEB_ORIGINS = Arrays.asList("*");
    private static final boolean DEFAULT_STANDARD_FLOW_ENABLED = true;
    private static final boolean DEFAULT_IMPLICIT_FLOW_ENABLED = false;
    private static final boolean DEFAULT_DIRECT_ACCESS_GRANTS_ENABLED = true;
    private static final boolean DEFAULT_SERVICE_ACCOUNTS_ENABLED = false;

    private final KeycloakUtil keycloakUtil;

    /**
     * Creates a public client in the realm.
     *
     * @param clientId    Client identifier
     * @param redirectUri Redirect URI for the client
     * @return true if client was created or already exists, false otherwise
     * @throws IllegalArgumentException if client ID is null or empty
     * @throws IllegalStateException    if tenant context is not set
     */
    @Override
    public boolean createPublicClient(String clientId, String redirectUri, String realmName) {
        validateClientId(clientId);
        //validateTenant();

        ClientRequest request = new ClientRequest();
        request.setClientId(clientId);
        request.setRedirectUri(redirectUri);
        request.setPublicClient(true);

        return createClient(request,realmName);
    }

    /**
     * Creates a confidential client in the realm.
     *
     * @param clientId     Client identifier
     * @param redirectUri  Redirect URI for the client
     * @param clientSecret Secret for the confidential client
     * @return true if client was created or already exists, false otherwise
     * @throws IllegalArgumentException if client ID or client secret is null or empty
     * @throws IllegalStateException    if tenant context is not set
     */
    @Override
    public boolean createConfidentialClient(String clientId, String redirectUri, String clientSecret, String realmName) {
        validateClientId(clientId);
        if (!StringUtils.hasText(clientSecret)) {
            throw new IllegalArgumentException("Client secret is required for confidential clients");
        }
        //validateTenant();

        ClientRequest request = new ClientRequest();
        request.setClientId(clientId);
        request.setRedirectUri(redirectUri);
        request.setPublicClient(false);
        request.setClientSecret(clientSecret);

        return createClient(request,realmName);
    }

    /**
     * Creates a client with advanced configuration options.
     *
     * @param request Client creation request with all configuration options
     * @return true if client was created or already exists, false otherwise
     * @throws IllegalArgumentException if client ID is null or empty
     * @throws IllegalStateException    if tenant context is not set
     */
    @Override
    public boolean createClient(ClientRequest request,String realmName) {
        validateClientId(request.getClientId());
        //validateTenant();

        String clientId = request.getClientId();
        boolean isPublic = request.isPublicClient();
        String redirectUri = request.getRedirectUri();

        log.info("Creating {} client '{}' with redirect URI: {}",
                isPublic ? "public" : "confidential", clientId, redirectUri);

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            ClientsResource clientsResource = keycloak.realm(realmName).clients();

            // Check if client already exists
            List<ClientRepresentation> existingClients = clientsResource.findByClientId(clientId);
            if (!existingClients.isEmpty()) {
                log.info("Client '{}' already exists in realm '{}'", clientId, realmName);

                // If update on existing is requested, update the client
                log.info("Updating existing client '{}' in realm '{}'", clientId, realmName);
                ClientRepresentation existingClient = existingClients.get(0);
                updateExistingClient(existingClient, request, clientsResource);
                createClientScope(keycloak, realmName);
                existingClient.getDefaultClientScopes().add("tenant-scope");
                return true;
            }

            // Create client representation
            ClientRepresentation client = buildClientRepresentation(request);

            // Create the client in Keycloak
            boolean isClientCreated = createClientInKeycloak(clientsResource, client, clientId);
            if (isClientCreated) {
                String clientDbId = clientsResource.findByClientId(clientId).get(0).getId();
                String scopeId = createClientScope(keycloak, realmName);
                clientsResource.get(clientDbId).addDefaultClientScope(scopeId);
            } else {
                log.error("Failed to create client '{}'", clientId);
                return false;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Rethrow validation exceptions
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating client '{}': {}", clientId, e.getMessage(), e);
            return false;
        }
        return true;
    }

    private String createClientScope(Keycloak keycloak, String realmName) {
        String scopeName = "tenant-scope";
        ClientScopeRepresentation scope = new ClientScopeRepresentation();
        scope.setName(scopeName);
        scope.setDescription("Adds tenantId claim to tokens");
        scope.setProtocol("openid-connect");

        // 3. Create the client scope in Keycloak
        try (Response response = keycloak.realm(realmName).clientScopes().create(scope)) {
            if (response.getStatus() == 201) {
                log.info("Client scope created successfully");
                // 2. Get client scope resource
                ClientScopesResource clientScopesResource = keycloak.realm(realmName).clientScopes();
                String scopeId = clientScopesResource.findAll().stream()
                        .filter(cs -> cs.getName().equals(scopeName))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Scope not found: " + scopeName))
                        .getId();

                ClientScopeResource clientScopeResource = clientScopesResource.get(scopeId);

                // 3. Create tenantId mapper
                ProtocolMapperRepresentation tenantMapper = new ProtocolMapperRepresentation();
                tenantMapper.setName("tenantId-mapper");
                tenantMapper.setProtocol("openid-connect");
                tenantMapper.setProtocolMapper("oidc-usermodel-attribute-mapper");

                Map<String, String> config = new HashMap<>();
                config.put("user.attribute", "tenantid");   // User attribute in Keycloak
                config.put("claim.name", "tenantid");      // Claim name in token
                config.put("jsonType.label", "String");     // Claim type
                config.put("id.token.claim", "true");       // Add to ID token
                config.put("access.token.claim", "true");   // Add to Access token

                tenantMapper.setConfig(config);

                // 4. Add mapper to client scope
                try (Response mapperResponse = clientScopeResource.getProtocolMappers().createMapper(tenantMapper)) {
                    if (mapperResponse.getStatus() == HttpStatus.SC_CREATED) {
                        log.info("Mapper added");
                        return scopeId;
                    }
                }

            }
            return null;
        }
    }

    /**
     * Gets a client by its client ID.
     *
     * @param clientId Client identifier
     * @return ClientRepresentation if found, null otherwise
     * @throws IllegalArgumentException if client ID is null or empty
     * @throws IllegalStateException    if tenant context is not set
     */
    @Override
    public ClientRepresentation getClientByClientId(String clientId) {
        validateClientId(clientId);
        validateTenant();

        String realmName = TenantContext.getTenantId();
        log.debug("Getting client '{}' from realm '{}'", clientId, realmName);

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            List<ClientRepresentation> clients = keycloak.realm(realmName).clients().findByClientId(clientId);

            if (clients.isEmpty()) {
                log.debug("Client '{}' not found in realm '{}'", clientId, realmName);
                return null;
            }

            return clients.get(0);
        } catch (Exception e) {
            log.error("Error retrieving client '{}' from realm '{}': {}",
                    clientId, realmName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deletes a client by its client ID.
     *
     * @param clientId Client identifier
     * @return true if client was deleted or didn't exist, false if deletion failed
     * @throws IllegalArgumentException if client ID is null or empty
     * @throws IllegalStateException    if tenant context is not set
     */
    @Override
    public boolean deleteClient(String clientId) {
        validateClientId(clientId);
        validateTenant();

        String realmName = TenantContext.getTenantId();
        log.info("Deleting client '{}' from realm '{}'", clientId, realmName);

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            ClientsResource clientsResource = keycloak.realm(realmName).clients();
            List<ClientRepresentation> clients = clientsResource.findByClientId(clientId);

            if (clients.isEmpty()) {
                log.info("Client '{}' not found in realm '{}', nothing to delete", clientId, realmName);
                return true;
            }

            String id = clients.get(0).getId();
            clientsResource.get(id).remove();
            log.info("Successfully deleted client '{}' from realm '{}'", clientId, realmName);
            return true;
        } catch (Exception e) {
            log.error("Error deleting client '{}' from realm '{}': {}",
                    clientId, realmName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets all clients in the realm.
     *
     * @return List of ClientRepresentation objects
     * @throws IllegalStateException if tenant context is not set
     */
    @Override
    public List<ClientRepresentation> getAllClients() {
        validateTenant();

        String realmName = TenantContext.getTenantId();
        log.debug("Getting all clients from realm '{}'", realmName);

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            return keycloak.realm(realmName).clients().findAll();
        } catch (Exception e) {
            log.error("Error retrieving clients from realm '{}': {}",
                    realmName, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Regenerates the client secret for a confidential client.
     *
     * @param clientId Client identifier
     * @return The new client secret, or null if regeneration failed
     * @throws IllegalArgumentException if client ID is null or empty
     * @throws IllegalStateException    if tenant context is not set
     */
    @Override
    public String regenerateClientSecret(String clientId) {
        validateClientId(clientId);
        validateTenant();

        String realmName = TenantContext.getTenantId();
        log.info("Regenerating client secret for client '{}' in realm '{}'", clientId, realmName);

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            RealmResource realmResource = keycloak.realm(realmName);
            ClientsResource clientsResource = realmResource.clients();

            List<ClientRepresentation> clients = clientsResource.findByClientId(clientId);
            if (clients.isEmpty()) {
                log.error("Client '{}' not found in realm '{}'", clientId, realmName);
                return null;
            }

            ClientRepresentation client = clients.get(0);
            if (client.isPublicClient()) {
                log.error("Cannot regenerate client secret for public client '{}'", clientId);
                return null;
            }

            String id = client.getId();
            ClientResource clientResource = clientsResource.get(id);

            // Regenerate the secret
            String secret = clientResource.generateNewSecret().getValue();
            log.info("Successfully regenerated client secret for client '{}' in realm '{}'",
                    clientId, realmName);

            return secret;
        } catch (Exception e) {
            log.error("Error regenerating client secret for client '{}' in realm '{}': {}",
                    clientId, realmName, e.getMessage(), e);
            return null;
        }
    }

    // Private helper methods

    /**
     * Validates client ID.
     *
     * @param clientId Client identifier to validate
     * @throws IllegalArgumentException if client ID is null or empty
     */
    private void validateClientId(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }
    }

    /**
     * Validates tenant context.
     *
     * @throws IllegalStateException if tenant context is not set
     */
    private void validateTenant() {
        if (!StringUtils.hasText(TenantContext.getTenantId())) {
            throw new IllegalStateException("Tenant ID is not set in TenantContext");
        }
    }

    /**
     * Builds a client representation from the request.
     *
     * @param request Client request with configuration options
     * @return ClientRepresentation configured according to the request
     */
    private ClientRepresentation buildClientRepresentation(ClientRequest request) {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(request.getClientId());
        client.setName(request.getClientId());
        client.setEnabled(request.isEnabled());
        // Configure access type (public/confidential)
        boolean isPublic = request.isPublicClient();
        if (isPublic) {
            client.setPublicClient(true);
            client.setBearerOnly(false);
        } else {
            client.setPublicClient(false);
            client.setBearerOnly(false);
            client.setClientAuthenticatorType(CLIENT_SECRET_AUTHENTICATOR);
            client.setSecret(request.getClientSecret());

            // Enable service accounts for confidential clients if requested
            client.setServiceAccountsEnabled(DEFAULT_SERVICE_ACCOUNTS_ENABLED);
        }

        // Configure redirect URIs
        if (StringUtils.hasText(request.getRedirectUri())) {
            List<String> redirectUris = new ArrayList<>();
            redirectUris.add(request.getRedirectUri());


            client.setRedirectUris(redirectUris);

            // Set web origins
            client.setWebOrigins(DEFAULT_WEB_ORIGINS);
        }

        // Configure authentication flows
        client.setStandardFlowEnabled(DEFAULT_STANDARD_FLOW_ENABLED);

        client.setImplicitFlowEnabled(DEFAULT_IMPLICIT_FLOW_ENABLED);

        client.setDirectAccessGrantsEnabled(DEFAULT_DIRECT_ACCESS_GRANTS_ENABLED);


        return client;
    }

    /**
     * Updates an existing client with new values from the request.
     *
     * @param existingClient  Existing client representation
     * @param request         Client request with new values
     * @param clientsResource Clients resource for the realm
     * @return true if update was successful, false otherwise
     */
    private boolean updateExistingClient(ClientRepresentation existingClient,
                                         ClientRequest request,
                                         ClientsResource clientsResource) {
        try {
            String id = existingClient.getId();


            if (request.isEnabled() != existingClient.isEnabled()) {
                existingClient.setEnabled(request.isEnabled());
            }

            // Update redirect URIs if provided
            if (StringUtils.hasText(request.getRedirectUri())) {
                List<String> redirectUris = new ArrayList<>();
                redirectUris.add(request.getRedirectUri());


                existingClient.setRedirectUris(redirectUris);
            }

            // Update web origins if provided
            existingClient.setSecret(request.getClientSecret());

            // Update the client
            clientsResource.get(id).update(existingClient);
            //addTenantIdMapper(clientsResource, request.getClientId(), id);
            log.info("Successfully updated client '{}'", request.getClientId());
            return true;

        } catch (Exception e) {
            log.error("Error updating client '{}': {}", request.getClientId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Creates a client in Keycloak.
     *
     * @param clientsResource Clients resource for the realm
     * @param client          Client representation to create
     * @param clientId        Client identifier for logging
     * @return true if creation was successful or client already exists, false otherwise
     */
    private boolean createClientInKeycloak(ClientsResource clientsResource,
                                           ClientRepresentation client,
                                           String clientId) {
        try (Response response = clientsResource.create(client)) {
            if (response.getStatus() == CREATED_STATUS) {
                log.info("Successfully created {} client '{}'",
                        client.isPublicClient() ? "public" : "confidential", clientId);
                // Fetch created client ID from Keycloak
                //addTenantIdMapper(clientsResource, clientId, clientDbId);
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


    private void addTenantIdMapper(ClientsResource clientsResource, String clientId, String clientDbId) {
        ClientResource clientResource = clientsResource.get(clientDbId);

        // Check if mapper already exists to avoid duplicates
        List<ProtocolMapperRepresentation> existingMappers = clientResource.getProtocolMappers().getMappers();
        boolean alreadyExists = existingMappers.stream()
                .anyMatch(mapper -> "tenantid".equals(mapper.getName()));

        if (alreadyExists) {
            log.info("TenantId mapper already exists for client '{}'", clientId);
            return;
        }

        // Create mapper
        ProtocolMapperRepresentation mapper = getProtocolMapperRepresentation();

        try (Response response = clientResource.getProtocolMappers().createMapper(mapper)) {
            log.info("TenantId mapper added for client '{}'", clientId);
            log.info("Tenant Id mapped to client {} ", response.getStatus());
        }
    }

    private static ProtocolMapperRepresentation getProtocolMapperRepresentation() {
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setName("tenantid");
        mapper.setProtocol("openid-connect");
        mapper.setProtocolMapper("oidc-usermodel-attribute-mapper");

        Map<String, String> config = new HashMap<>();
        config.put("user.attribute", "tenantid");
        config.put("claim.name", "tenantid");
        config.put("jsonType.label", "String");
        config.put("id.token.claim", "true");
        config.put("access.token.claim", "true");
        mapper.setConfig(config);
        return mapper;
    }

}
