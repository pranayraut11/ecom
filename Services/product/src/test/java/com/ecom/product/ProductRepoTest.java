package com.ecom.product;

import com.ecom.product.entity.Product;
import com.ecom.product.repository.specification.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class ProductRepoTest {

   @ServiceConnection
   static MongoDBContainer mongodb = new MongoDBContainer("mongo:6.0");

   static {
      mongodb.start();
   }
   @Autowired
   private ProductRepository productRepository;

   @Test
   void test(){
      Product product = new Product();
      product.setName("Pranay");
      Product product1 = productRepository.save(product);
      System.out.println("Product id:"+product1.getId());
      Assertions.assertNotNull(product1);
   }
}