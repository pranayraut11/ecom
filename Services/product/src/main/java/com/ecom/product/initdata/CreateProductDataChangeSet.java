package com.ecom.product.initdata;

import com.ecom.product.entity.Product;
import com.ecom.product.entity.Price;
import com.ecom.product.model.Seller;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

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
            product.setDescription("Smart TV with 4K resolution, HDR, and streaming apps. Model: "+i);
            product.setImages(List.of(
                "https://example.com/images/tv"+i+"_1.jpg",
                "https://example.com/images/tv"+i+"_2.jpg"
            ));

            Price price1 = new Price();
            price1.setPrice(new java.math.BigDecimal("49999.99"));
            price1.setDiscountedPrice(new java.math.BigDecimal("44999.99"));
            price1.setDiscount(new java.math.BigDecimal("5000"));
            price1.setInStock(i % 2 == 0);
            Seller seller1 = new Seller();
            seller1.setId("sellerA");
            seller1.setName("ElectroMart");
            seller1.setRating(4.5f);
            price1.setSeller(seller1);

            Price price2 = new Price();
            price2.setPrice(new java.math.BigDecimal("52000.00"));
            price2.setDiscountedPrice(new java.math.BigDecimal("47000.00"));
            price2.setDiscount(new java.math.BigDecimal("5000"));
            price2.setInStock(true);
            Seller seller2 = new Seller();
            seller2.setId("sellerB");
            seller2.setName("MegaStore");
            seller2.setRating(4.2f);
            price2.setSeller(seller2);

            product.setPrices(List.of(price1, price2));
            mongoTemplate.save(product);
        }


    }

    @RollbackExecution
    public void rollback(){

    }
}
