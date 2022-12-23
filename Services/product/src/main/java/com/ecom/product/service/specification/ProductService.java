package com.ecom.product.service.specification;

import com.ecom.product.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAll();

    ProductDTO get(String id);

    void delete(String id);

    void create(ProductDTO productDTO);

    void update(ProductDTO entity);
}
