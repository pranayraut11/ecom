package com.ecom.product;

import com.ecom.product.configuration.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestProductApplication {

    public static void main(String[] args) {
        SpringApplication.from(CatalogApplication::main).with(ContainersConfig.class).run(args);
    }

}