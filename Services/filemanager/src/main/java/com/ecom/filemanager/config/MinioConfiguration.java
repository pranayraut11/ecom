package com.ecom.filemanager.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String secretKey;

    @Value("${minio.host}")
    private String minioUrl;

    @Value("${minio.port}")
    private int port;


    @Bean
    public MinioClient minioClient(){
        return new MinioClient.Builder().credentials(accessKey,secretKey).endpoint(minioUrl,port,false).build();
    }

}
