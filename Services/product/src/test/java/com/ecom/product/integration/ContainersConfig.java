package com.ecom.product.integration;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
public class ContainersConfig {

    /**
     * For container to container communication DOCKER NETWORK is required(Should use withNetworkAliases)
     */
    Network network = Network.newNetwork();

    @Bean
    @ServiceConnection
    public MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer("mongo:6.0");
    }

    @Bean
    public GenericContainer minioContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
        GenericContainer minio = new GenericContainer("minio/minio")
                .withEnv("MINIO_ROOT_USER", "miniouser").withEnv("MINIO_ROOT_PASSWORD", "miniouser")
                .withCommand("server /data")
                .withNetwork(network).withNetworkAliases("minioserver").withExposedPorts(9000)
                .waitingFor(new HttpWaitStrategy()
                        .forPath("/minio/health/ready")
                        .forPort(9000)
                        .withStartupTimeout(Duration.ofSeconds(10)));
        minio.withLogConsumer(new Slf4jLogConsumer(log));
        return minio;
    }

    @Bean
    public GenericContainer fileManagerServiceContainer(DynamicPropertyRegistry registry) {
        GenericContainer fileManagerContainer = new GenericContainer("pranayraut11/filemanager-app")
                .withEnv("minio.host", "minioserver").withEnv("minio.port","9000")
                .withExposedPorts(8080).withNetwork(network)
                .waitingFor(new HttpWaitStrategy()
                        .forPath("/app/started")
                        .forPort(8080)
                        .withStartupTimeout(Duration.ofSeconds(20)));
        fileManagerContainer.withLogConsumer(new Slf4jLogConsumer(log));

        registry.add("app.service.filemanager.host", fileManagerContainer::getHost);
        registry.add("app.service.filemanager.port", fileManagerContainer::getFirstMappedPort);
        return fileManagerContainer;
    }

}
