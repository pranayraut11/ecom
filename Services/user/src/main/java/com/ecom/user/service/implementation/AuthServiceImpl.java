package com.ecom.user.service.implementation;

import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.exception.custom.UserServiceException;
import com.ecom.user.repository.UserRepository;
import com.ecom.user.rest.AuthRestService;
import com.ecom.user.service.specification.UserAuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements UserAuthService {

    private final AuthRestService keycloakAuthService;

    private final AuthClientDetails userCredentials;

    private final UserRepository userRepository;

    @Value("${auth.realm}")
    private String realm;

    public AuthServiceImpl(AuthRestService keycloakAuthService, @Qualifier("userClientCredentials") AuthClientDetails userCredentials, UserRepository userRepository) {
        this.keycloakAuthService = keycloakAuthService;
        this.userCredentials = userCredentials;
        this.userRepository = userRepository;
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

    @Override
    public boolean exists(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            throw new UserServiceException("Failed to check user existence", e);
        }
    }
}
