package com.ecom.user.service.specification;

import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import org.keycloak.common.VerificationException;

public interface UserAuthService {
     TokenDetails login(Login login) throws VerificationException;

     void logout(String token);

     boolean exists(String email);

}
