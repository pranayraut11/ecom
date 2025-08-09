package com.ecom.authprovider.manager.api;

import com.ecom.authprovider.dto.request.ClientRequest;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.List;

/**
 * Interface for client management operations.
 */
public interface ClientManager {

    /**
     * Creates a public client in the realm.
     *
     * @param clientId Client identifier
     * @param redirectUri Redirect URI for the client
     * @return true if client was created or already exists, false otherwise
     */
    boolean createPublicClient(String clientId, String redirectUri);

    /**
     * Creates a confidential client in the realm.
     *
     * @param clientId Client identifier
     * @param redirectUri Redirect URI for the client
     * @param clientSecret Secret for the confidential client
     * @return true if client was created or already exists, false otherwise
     */
    boolean createConfidentialClient(String clientId, String redirectUri, String clientSecret);

    boolean createClient(ClientRequest request);

    ClientRepresentation getClientByClientId(String clientId);

    boolean deleteClient(String clientId);

    List<ClientRepresentation> getAllClients();

    String regenerateClientSecret(String clientId);
}
