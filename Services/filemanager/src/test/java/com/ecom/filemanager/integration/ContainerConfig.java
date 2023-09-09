package com.ecom.filemanager.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
public class ContainerConfig {


    @Bean
    @ServiceConnection
    public MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer("mongo:6.0");
    }


    @Bean
    public GenericContainer minioContainer(DynamicPropertyRegistry dynamicPropertyRegistry){
        GenericContainer minio = new GenericContainer("minio/minio")
                .withEnv("MINIO_ROOT_USER","miniouser").withEnv("MINIO_ROOT_PASSWORD","miniouser")
                .withCommand("server /data")
                .withExposedPorts(9000)
                .waitingFor(new HttpWaitStrategy()
                        .forPath("/minio/health/ready")
                        .forPort(9000)
                        .withStartupTimeout(Duration.ofSeconds(10)));
        dynamicPropertyRegistry.add("minio.host",minio::getHost);
        dynamicPropertyRegistry.add("minio.port",minio::getFirstMappedPort);
        return minio;
    }
}
