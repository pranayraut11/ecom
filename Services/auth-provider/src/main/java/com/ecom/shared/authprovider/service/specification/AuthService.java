package com.ecom.shared.authprovider.service.specification;

import com.ecom.shared.authprovider.dto.UserCreateDTO;
import org.apache.http.auth.AuthenticationException;

public interface AuthService {
    void login(String username, String password) throws AuthenticationException;
    void logout(String token) throws AuthenticationException;
    boolean exists(String email) throws AuthenticationException;
    String getUserId(String token) throws AuthenticationException;
    String getUserEmail(String token) throws AuthenticationException;
    void createUser(UserCreateDTO userCreateDTO) throws AuthenticationException;

}
