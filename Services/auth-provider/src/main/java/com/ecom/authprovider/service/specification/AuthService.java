package com.ecom.authprovider.service.specification;

import com.ecom.authprovider.dto.request.LoginRequest;
import com.ecom.authprovider.dto.request.LogoutRequest;
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

    /**
     * Logs out a user by invalidating their refresh token
     *
     * @param logoutRequest the logout request containing the refresh token
     * @return true if logout was successful, false otherwise
     */
    boolean logout(LogoutRequest logoutRequest);
}
