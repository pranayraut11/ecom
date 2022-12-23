package com.ecom.user.service.specification;

import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import org.keycloak.common.VerificationException;

public interface AuthService {
    public TokenDetails login(Login login) throws VerificationException;

    public void logout(String token);

}
