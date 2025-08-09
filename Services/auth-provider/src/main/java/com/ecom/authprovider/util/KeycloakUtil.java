package com.ecom.authprovider.util;

import com.ecom.authprovider.config.KeycloakConnectionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

/**
 * Utility class for creating Keycloak instances with configuration from properties.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakUtil {

    private final KeycloakConnectionProperties properties;

    /**
     * Creates a Keycloak admin client using admin credentials from properties file.
     *
     * @return Keycloak admin client instance
     */
    public Keycloak createAdminClient() {
        log.info("Creating Keycloak admin client for server: {}", properties.getServerUrl());
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(properties.getMasterRealm())
                .username(properties.getAdmin().getUsername())
                .password(properties.getAdmin().getPassword())
                .clientId(properties.getAdmin().getClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    /**
     * Creates a Keycloak client for a specific user in the specified realm.
     *
     * @param realm The realm to authenticate against
     * @param username The username for authentication
     * @param password The password for authentication
     * @param clientId The client ID to use (optional, uses default from properties if null)
     * @return Keycloak client instance
     */
    public Keycloak createUserClient(String realm, String username, String password, String clientId) {
        String clientToUse = clientId != null ? clientId : properties.getClientId();
        log.info("Creating Keycloak user client for user {} in realm {}", username, realm);
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientToUse)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    /**
     * Creates a Keycloak client for client credentials flow.
     *
     * @param realm The realm to authenticate against
     * @param clientId The client ID (optional, uses default from properties if null)
     * @param clientSecret The client secret (optional, uses default from properties if null)
     * @return Keycloak client instance
     */
    public Keycloak createClientCredentialsClient(String realm, String clientId, String clientSecret) {
        String clientToUse = clientId != null ? clientId : properties.getClientId();
        String secretToUse = clientSecret != null ? clientSecret : properties.getClientSecret();

        log.info("Creating Keycloak client credentials client for client {} in realm {}", clientToUse, realm);
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(realm)
                .clientId(clientToUse)
                .clientSecret(secretToUse)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
