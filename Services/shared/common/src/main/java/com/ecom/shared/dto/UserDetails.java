package com.ecom.shared.dto;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;

import java.util.Objects;

@Slf4j
public final class UserDetails {

    private static String userId;
    private UserDetails()  {
    }

    public static void setUserInfo(String token) throws VerificationException {
        AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();
        if (Objects.nonNull(accessToken.getRealmAccess())) {

            log.info("UserId {}", accessToken.getSubject());
            userId = accessToken.getSubject();
        }
    }
    public static String getUserId() {
        return userId;
    }
}
