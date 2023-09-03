package com.ecom.product.integration.controller;


import com.ecom.product.controller.ProductController;
import com.ecom.product.integration.ContainersConfig;
import com.ecom.shared.common.utility.FileUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Slf4j
@Import(ContainersConfig.class)
public class ProductControllerTest {

    @Autowired
    private ProductController productController;


    @Test
    void createProduct() throws JsonProcessingException {
        MultipartFile[] multipartFiles = new MultipartFile[0];
        String productJson = FileUtility.getJson("Products.json");
        productController.create(multipartFiles ,productJson);
    }
}
