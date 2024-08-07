package com.ecom.user.service.implementation;

import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.rest.KeycloakAuthService;
import com.ecom.user.service.specification.AuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private KeycloakAuthService keycloakAuthService;

    private AuthClientDetails userCredentials;

    @Value("${auth.realm}")
    private String realm;

    public AuthServiceImpl(KeycloakAuthService keycloakAuthService,@Qualifier("userClientCredentials") AuthClientDetails userCredentials) {
        this.keycloakAuthService = keycloakAuthService;
        this.userCredentials = userCredentials;
    }

    @Override
    public TokenDetails login(Login login) {
        userCredentials.setUsername(login.getUsername());
        userCredentials.setPassword(login.getPassword());
        return keycloakAuthService.login(userCredentials,realm);
    }

    @Override
    public void logout(String token) {
        keycloakAuthService.logout(token,realm);
    }
}
