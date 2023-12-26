package com.ecom.product.initdata;

import com.ecom.product.entity.Product;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id="001",order = "001",author = "Pranay")
public class CreateProductDataChangeSet {


    private final MongoTemplate mongoTemplate;
    public CreateProductDataChangeSet(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void execute(){
        for (int i = 1; i < 60; i++) {
            Product product = new Product();
            product.setId("00"+i);
            product.setName("Tv "+i);
            mongoTemplate.save(product);
        }


    }

    @RollbackExecution
    public void rollback(){

    }
}
