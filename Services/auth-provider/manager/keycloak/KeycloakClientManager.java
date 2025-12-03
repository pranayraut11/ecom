    @Override
    public boolean createClient(ClientRequest request, String realmName) {
        validateClientId(request.getClientId());
        String clientId = request.getClientId();
        boolean isPublic = request.isPublicClient();
        String redirectUri = request.getRedirectUri();
        log.info("Creating {} client '{}' with redirect URI: {}", isPublic ? "public" : "confidential", clientId, redirectUri);

        boolean newlyCreated = false;
        String clientDbId = null;

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            ClientsResource clientsResource = keycloak.realm(realmName).clients();
            List<ClientRepresentation> existingClients = clientsResource.findByClientId(clientId);
            if (!existingClients.isEmpty()) {
                // Update existing client and try to attach scope
                ClientRepresentation existingClient = existingClients.get(0);
                boolean updated = updateExistingClient(existingClient, request, clientsResource);
                attachScopeToClient(keycloak, clientsResource, existingClient.getId(), realmName, clientId, false);
                return updated;
            }
            // Create new client
            ClientRepresentation client = buildClientRepresentation(request);
            boolean isClientCreated = createClientInKeycloak(clientsResource, client, clientId);
            if (!isClientCreated) {
                log.error("Failed to create client '{}'", clientId);
                return false;
            }
            newlyCreated = true;
            List<ClientRepresentation> createdList = clientsResource.findByClientId(clientId);
            if (createdList.isEmpty()) {
                log.error("Client '{}' was created but cannot be found in realm '{}'", clientId, realmName);
                return false;
            }
            clientDbId = createdList.get(0).getId();
            // Create and attach scope, rollback if any step fails
            if (!attachScopeToClient(keycloak, clientsResource, clientDbId, realmName, clientId, true)) {
                rollbackCreatedClient(clientsResource, clientDbId, clientId, realmName);
                return false;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating client '{}': {}", clientId, e.getMessage(), e);
            if (newlyCreated && clientDbId != null) {
                try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
                    rollbackCreatedClient(keycloak.realm(realmName).clients(), clientDbId, clientId, realmName);
                } catch (Exception ex) {
                    log.error("Failed to obtain admin client for rollback of client '{}': {}", clientId, ex.getMessage(), ex);
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Creates client scope and attaches it to the client. Returns true if successful, false otherwise.
     * If isNewClient is true, failure is considered critical (for rollback).
     */
    private boolean attachScopeToClient(Keycloak keycloak, ClientsResource clientsResource, String clientDbId, String realmName, String clientId, boolean isNewClient) {
        try {
            String scopeId = createClientScope(keycloak, realmName);
            if (scopeId == null) {
                log.error("Client scope creation failed for client '{}' in realm '{}'", clientId, realmName);
                return false;
            }
            try {
                clientsResource.get(clientDbId).addDefaultClientScope(scopeId);
                return true;
            } catch (Exception e) {
                log.error("Failed to attach client scope to client '{}': {}", clientId, e.getMessage(), e);
                return false;
            }
        } catch (Exception e) {
            log.error("Error during scope creation/attach for client '{}': {}", clientId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Deletes a newly created client for rollback purposes.
     */
    private void rollbackCreatedClient(ClientsResource clientsResource, String clientDbId, String clientId, String realmName) {
        try {
            clientsResource.get(clientDbId).remove();
            log.info("Rolled back created client '{}' in realm '{}' due to failure in subsequent steps", clientId, realmName);
        } catch (Exception e) {
            log.error("Failed to rollback client '{}' in realm '{}': {}", clientId, realmName, e.getMessage(), e);
        }
    }
// ...existing code...

