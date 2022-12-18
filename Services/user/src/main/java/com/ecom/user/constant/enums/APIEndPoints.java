package com.ecom.user.constant.enums;

public final class APIEndPoints {

    private APIEndPoints() {
    }

    public static final String KEYCLOAK_TOKEN_URL = "realms/{realms}/protocol/openid-connect/token";
    public static final String KEYCLOAK_CREATE_USER_URL = "admin/realms/{realms}/users";

    public static final String KEYCLOAK_LOGOUT = "/realms/{realms}/protocol/openid-connect/logout";
}
