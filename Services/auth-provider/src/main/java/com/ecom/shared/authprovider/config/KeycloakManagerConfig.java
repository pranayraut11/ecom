package com.ecom.shared.authprovider.config;

import com.ecom.shared.authprovider.keycloak.KeycloakAdminClientFactory;
import com.ecom.shared.authprovider.keycloak.KeycloakClientManager;
import com.ecom.shared.authprovider.keycloak.KeycloakRealmManager;
import com.ecom.shared.authprovider.keycloak.KeycloakRoleManager;
import com.ecom.shared.authprovider.keycloak.KeycloakUserManager;
import com.ecom.shared.authprovider.keycloak.api.ClientManager;
import com.ecom.shared.authprovider.keycloak.api.RealmManager;
import com.ecom.shared.authprovider.keycloak.api.RoleManager;
import com.ecom.shared.authprovider.keycloak.api.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Keycloak-related beans.
 */
@Configuration
@Slf4j
public class KeycloakManagerConfig {

    @Value("${keycloak.default-realm:master}")
    private String defaultRealm;

    /**
     * Creates a RealmManager bean.
     *
     * @return the RealmManager implementation
     */
    @Bean
    public RealmManager realmManager() {
        Keycloak keycloak = KeycloakAdminClientFactory.getKeycloakClient();
        return new KeycloakRealmManager(keycloak);
    }

    /**
     * Creates a factory method that returns a RoleManager for a specific realm.
     * This allows dynamic realm selection at runtime.
     *
     * @return a factory that produces RoleManagers for specific realms
     */
    @Bean
    public RoleManagerFactory roleManagerFactory() {
        return new RoleManagerFactory();
    }

    /**
     * Creates a factory method that returns a ClientManager for a specific realm.
     * This allows dynamic realm selection at runtime.
     *
     * @return a factory that produces ClientManagers for specific realms
     */
    @Bean
    public ClientManagerFactory clientManagerFactory() {
        return new ClientManagerFactory();
    }

    /**
     * Creates a factory method that returns a UserManager for a specific realm.
     * This allows dynamic realm selection at runtime.
     *
     * @return a factory that produces UserManagers for specific realms
     */
    @Bean
    public UserManagerFactory userManagerFactory() {
        return new UserManagerFactory();
    }

    /**
     * Factory class for creating RoleManager instances for specific realms.
     */
    public static class RoleManagerFactory {
        /**
         * Gets a RoleManager for the specified realm.
         *
         * @param realmName the name of the realm
         * @return a RoleManager configured for the specified realm
         */
        public RoleManager getManager(String realmName) {
            Keycloak keycloak = KeycloakAdminClientFactory.getKeycloakClient();
            RealmResource realmResource = keycloak.realm(realmName);
            return new KeycloakRoleManager(realmResource, keycloak);
        }
    }

    /**
     * Factory class for creating ClientManager instances for specific realms.
     */
    public static class ClientManagerFactory {
        /**
         * Gets a ClientManager for the specified realm.
         *
         * @param realmName the name of the realm
         * @return a ClientManager configured for the specified realm
         */
        public ClientManager getManager(String realmName) {
            Keycloak keycloak = KeycloakAdminClientFactory.getKeycloakClient();
            RealmResource realmResource = keycloak.realm(realmName);
            return new KeycloakClientManager(realmResource, keycloak);
        }
    }

    /**
     * Factory class for creating UserManager instances for specific realms.
     */
    public static class UserManagerFactory {
        /**
         * Gets a UserManager for the specified realm.
         *
         * @param realmName the name of the realm
         * @param roleManager the RoleManager to use
         * @return a UserManager configured for the specified realm
         */
        public UserManager getManager(String realmName, RoleManager roleManager) {
            Keycloak keycloak = KeycloakAdminClientFactory.getKeycloakClient();
            RealmResource realmResource = keycloak.realm(realmName);
            return new KeycloakUserManager(realmResource, roleManager, keycloak);
        }
    }
}
