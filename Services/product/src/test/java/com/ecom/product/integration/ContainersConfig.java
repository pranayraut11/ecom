package com.ecom.product.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    public MongoDBContainer mongoDBContainer() {
      return new MongoDBContainer("mongo:6.0");
    }
    @Bean
    public GenericContainer fileManagerServiceContainer(DynamicPropertyRegistry registry){
        GenericContainer fileManagerContainer = new GenericContainer("pranayraut11/filemanager-app").withExposedPorts(8080);
        registry.add("app.service.filemanager.host",fileManagerContainer::getHost);
        registry.add("app.service.filemanager.port",fileManagerContainer::getFirstMappedPort);
        return fileManagerContainer;
    }


}
