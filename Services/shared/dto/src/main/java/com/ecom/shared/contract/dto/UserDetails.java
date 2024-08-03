package com.ecom.shared.contract.dto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class UserDetails {

    private static String userId;
    private UserDetails()  {
    }

    public static void setUserInfo(String token) throws Exception {
//        AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();
//        if (Objects.nonNull(accessToken.getRealmAccess())) {
//
//            log.info("UserId {}", accessToken.getSubject());
//            userId = accessToken.getSubject();
//        }
    }
    public static String getUserId() {
        return userId;
    }
}
