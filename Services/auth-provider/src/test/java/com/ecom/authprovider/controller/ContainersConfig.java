package com.ecom.authprovider.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
public class ContainersConfig {

    /**
     * For container to container communication DOCKER NETWORK is required(Should use withNetworkAliases)
     */
    Network network = Network.newNetwork();

    @Bean
    public GenericContainer<?> keycloakContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
        GenericContainer<?> keycloak = new GenericContainer<>("quay.io/keycloak/keycloak:24.0.3")
                .withExposedPorts(8080).withNetwork(network)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .withCommand("start-dev")
                .waitingFor(Wait.forHttp("/admin").withStartupTimeout(Duration.ofMinutes(2)))
                .withLogConsumer(new Slf4jLogConsumer(log)).withCreateContainerCmdModifier(cmd -> cmd.withName("keycloak"));

        dynamicPropertyRegistry.add("keycloak.server-url", () -> "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080));
        dynamicPropertyRegistry.add("keycloak.admin.username", () -> "admin");
        dynamicPropertyRegistry.add("keycloak.admin.password", () -> "admin");
        dynamicPropertyRegistry.add("keycloak.admin.client-id", () -> "admin-cli");
        dynamicPropertyRegistry.add("keycloak.master-realm", () -> "master");

        return keycloak;
    }

    @Bean
    public GenericContainer<?> mongoGenericContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
        GenericContainer<?> mongo = new GenericContainer<>("mongo:latest")
                .withNetwork(network)
                .withExposedPorts(27017)
                .withStartupAttempts(3)
                .withStartupTimeout(Duration.ofSeconds(30))
                .withLogConsumer(new Slf4jLogConsumer(log));
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", () -> "mongodb://" + mongo.getHost() + ":" + mongo.getMappedPort(27017) + "/test");
        return mongo;
    }
}
