package com.ecom.user.service.implementation;

import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.rest.KeycloakAuthService;
import com.ecom.user.service.specification.AuthService;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private KeycloakAuthService keycloakAuthService;

    private AuthClientDetails userCredentials;

    @Value("${user.auth.realms.sub_realms}")
    private String subRealms;

    public AuthServiceImpl(KeycloakAuthService keycloakAuthService,@Qualifier("userClientCredentials") AuthClientDetails userCredentials) {
        this.keycloakAuthService = keycloakAuthService;
        this.userCredentials = userCredentials;
    }

    @Override
    public TokenDetails login(Login login) throws VerificationException {
        userCredentials.setUsername(login.getUsername());
        userCredentials.setPassword(login.getPassword());
        return keycloakAuthService.login(userCredentials,subRealms);
    }

    @Override
    public void logout(String token) {
        keycloakAuthService.logout(token,subRealms);
    }
}
