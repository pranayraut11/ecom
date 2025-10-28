package com.ecom.shared.common.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;

import java.util.Map;

public class TokenUtils {
    private TokenUtils(){
    }
    public static String getTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public static String getUserId(String token) throws VerificationException {
        return getAccessToken(getTokenFromHeader(token)).getSubject();
    }

    public static AccessToken getAccessToken(String token) throws VerificationException {
        return TokenVerifier.create(token, AccessToken.class).getToken();
    }

    public static String extractTenantId(String token) throws VerificationException {
        AccessToken map = TokenVerifier.create(token, AccessToken.class).getToken();
        return "ss";
    }
}
