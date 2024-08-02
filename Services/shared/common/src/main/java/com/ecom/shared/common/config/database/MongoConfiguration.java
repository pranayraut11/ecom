package com.ecom.shared.common.config.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "spring.data.mongodb.enabled", havingValue = "true")
public class MongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUrl;
    @Value("${spring.mongodb.connection.max-size}")
    private int maxSize;

    @Value("${spring.mongodb.connection.min-size}")
    private int minSize;

    @Value("${spring.mongodb.connection.timeout}")
    private int timeout;
    @Value("${spring.mongodb.connection.max-ideal-time}")
    private int maxIdealTime;
    @Value("${spring.mongodb.connection.max-wait-time}")
    private int maxWaitTime;


    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MongoClientSettings.builder().applyConnectionString(new ConnectionString(mongoUrl))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(timeout, TimeUnit.SECONDS))
                .applyToConnectionPoolSettings(connectionPoolSettings->connectionPoolSettings.maxSize(maxSize)
                        .minSize(minSize).maxConnectionIdleTime(maxIdealTime,TimeUnit.SECONDS)
                        .maxWaitTime(maxWaitTime,TimeUnit.SECONDS))
                .build());
    }
}
