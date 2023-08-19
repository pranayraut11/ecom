package com.ecom.product.integration.controller;


import com.ecom.product.controller.ProductController;
import com.ecom.product.utility.FileUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
@Testcontainers
@Slf4j
public class ProductControllerTest {
    @ServiceConnection
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:6.0");

    static {
        mongodb.start();
    }

    @Autowired
    private ProductController productController;

    @Test
    void createProduct() throws JsonProcessingException {
        MultipartFile[] multipartFiles = new MultipartFile[0];
        String productJson = FileUtility.getJsonFromFile("Products.json");
        log.info(productJson);
        productController.create(multipartFiles ,productJson);
    }
}
