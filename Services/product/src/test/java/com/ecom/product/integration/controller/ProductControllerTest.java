package com.ecom.product.integration.controller;


import com.ecom.product.configuration.ContainersConfig;
import com.ecom.product.controller.ProductController;
import com.ecom.shared.common.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@Slf4j
@Import(ContainersConfig.class)
public class ProductControllerTest {

    @Autowired
    private ProductController productController;


    @Test
    void createProduct() throws IOException {
        File txtfile =   new File("src/test/resources/" + "images/tv.txt");
        if(txtfile.createNewFile()) {
            MultipartFile file = FileUtility.getMultipartFile("images/tv.txt");
            String productJson = FileUtility.getJson("Products.json");
            productController.create(List.of(file).toArray(new MultipartFile[0]), productJson);
        }
    }
}
