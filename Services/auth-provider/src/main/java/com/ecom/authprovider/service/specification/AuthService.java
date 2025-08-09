package com.ecom.authprovider.service.specification;

import com.ecom.authprovider.dto.request.LoginRequest;
import com.ecom.authprovider.dto.response.LoginResponse;

/**
 * Service interface for authentication operations
 */
public interface AuthService {

    /**
     * Authenticates a user with the provided credentials
     *
     * @param loginRequest the login credentials
     * @return the authentication response with tokens
     */
    LoginResponse login(LoginRequest loginRequest);
}
