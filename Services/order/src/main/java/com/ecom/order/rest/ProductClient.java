package com.ecom.order.rest;

import com.ecom.order.model.Product;
import com.ecom.shared.contract.dto.PageRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "catalog", fallbackFactory = ProductClient.ProductClientFallbackFactory.class)
public interface ProductClient {

    @PostMapping(value = "/products/filter")
    List<Product> getProducts(@RequestBody PageRequestDTO pageRequestDTO);

    @Component
    @Slf4j
    class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
        @Override
        public ProductClient create(Throwable cause) {
            log.error("Error {}", cause.getMessage());
            return new ProductClient() {
                @Override
                public List<Product> getProducts(PageRequestDTO pageRequestDTO) {
                    return List.of();
                }
            };
        }
    }
}
