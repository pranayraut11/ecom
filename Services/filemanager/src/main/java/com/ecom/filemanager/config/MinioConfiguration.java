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

    @Value("${minio.url}")
    private String minioUrl;


    @Bean
    public MinioClient minioClient(){
        return new MinioClient.Builder().credentials(accessKey,secretKey).endpoint(minioUrl,9000,false).build();
    }

}
