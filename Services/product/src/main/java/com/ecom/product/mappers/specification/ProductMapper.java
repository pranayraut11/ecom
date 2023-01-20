package com.ecom.product.mappers.specification;

import com.ecom.product.dto.ProductDTO;
import com.ecom.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", implementationPackage = "com.ecom.product.mappers.specification")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );


    ProductDTO productToProductDTO(Product product);

    Product productDTOToProduct(ProductDTO productDTO);
}
