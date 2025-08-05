package com.ecom.shared.authprovider.service.implementation;

import com.ecom.shared.authprovider.dto.UserCreateDTO;
import com.ecom.shared.authprovider.service.specification.AuthService;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KeycloakAuthService implements AuthService {

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;
    @Value("${keycloak.realm}")
    private String keycloakRealm;
    @Value("${keycloak.client-id}")
    private String keycloakClientId;
    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;
    @Value("${keycloak.admin.username}")
    private String keycloakAdminUsername;
    @Value("${keycloak.admin.password}")
    private String keycloakAdminPassword;

    @Override
    public void login(String username, String password) throws AuthenticationException {

    }

    @Override
    public void logout(String token) throws AuthenticationException {

    }

    @Override
    public boolean exists(String email) throws AuthenticationException {
        return false;
    }

    @Override
    public String getUserId(String token) throws AuthenticationException {
        return "";
    }

    @Override
    public String getUserEmail(String token) throws AuthenticationException {
        return "";
    }

    private Keycloak buildKeycloakClient() {
        return KeycloakBuilder.builder()
            .serverUrl(keycloakServerUrl)
            .realm(keycloakRealm)
            .clientId(keycloakClientId)
            .clientSecret(keycloakClientSecret)
            .username(keycloakAdminUsername)
            .password(keycloakAdminPassword)
            .build();
    }

    @Override
    public void createUser(UserCreateDTO userCreateDTO) throws AuthenticationException {
        try (Keycloak keycloak = buildKeycloakClient()) {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(userCreateDTO.getUsername());
            user.setEmail(userCreateDTO.getEmail());
            user.setFirstName(userCreateDTO.getFirstName());
            user.setLastName(userCreateDTO.getLastName());
            user.setEnabled(true);
            if (userCreateDTO.getAttributes() != null) {
                user.setAttributes(userCreateDTO.getAttributes());
            }
            try (Response response = keycloak.realm(keycloakRealm).users().create(user)) {
                log.info( "User created with response "+ response.getStatusInfo().getStatusCode());
            }
        }
    }
}
