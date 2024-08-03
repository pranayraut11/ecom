package com.ecom.wrapper.database.mongodb.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfiguration {
    @Value("${spring.data.mongodb.uri}")
    private String mongoUrl;
    @Value("${spring.mongodb.connection.max-size:60}")
    private int maxSize;

    @Value("${spring.mongodb.connection.min-size:6}")
    private int minSize;

    @Value("${spring.mongodb.connection.timeout:15}")
    private int timeout;
    @Value("${spring.mongodb.connection.max-ideal-time:300}")
    private int maxIdealTime;
    @Value("${spring.mongodb.connection.max-wait-time:60}")
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
