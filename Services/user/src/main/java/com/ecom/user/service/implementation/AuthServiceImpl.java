package com.ecom.user.service.implementation;

import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.rest.KeycloakAuthService;
import com.ecom.user.service.specification.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private KeycloakAuthService keycloakAuthService;
    private AuthClientDetails authClientDetails;

    @Value("${user.auth.realms.sub_realms}")
    private String subRealms;

    public AuthServiceImpl(KeycloakAuthService keycloakAuthService,AuthClientDetails authClientDetails) {
        this.keycloakAuthService = keycloakAuthService;
        this.authClientDetails = authClientDetails;
    }

    @Override
    public TokenDetails login(Login login) {
        authClientDetails.setUsername(login.getUsername());
        authClientDetails.setPassword(login.getPassword());
        return keycloakAuthService.login(authClientDetails,subRealms);
    }
}
