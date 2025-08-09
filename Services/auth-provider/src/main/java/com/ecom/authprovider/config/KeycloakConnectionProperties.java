package com.ecom.authprovider.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Keycloak connection.
 * Maps to properties with prefix "keycloak" in application.yml.
 */
@Component
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConnectionProperties {

    /**
     * Keycloak server URL
     */
    private String serverUrl;

    /**
     * Master realm name
     */
    private String masterRealm = "master";

    /**
     * Default realm for application
     */
    private String realm;

    /**
     * Default client ID for application
     */
    private String clientId;

    /**
     * Default client secret
     */
    private String clientSecret;

    /**
     * Admin user properties
     */
    private Admin admin = new Admin();

    /**
     * Nested class for admin properties
     */
    @Getter
    @Setter
    public static class Admin {
        /**
         * Admin username
         */
        private String username = "admin";

        /**
         * Admin password
         */
        private String password = "admin";

        /**
         * Admin client ID
         */
        private String clientId = "admin-cli";
    }
}
