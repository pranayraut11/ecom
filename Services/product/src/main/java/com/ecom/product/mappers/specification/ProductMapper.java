package com.ecom.product.mappers.specification;

import com.ecom.product.dto.ProductDTO;
import com.ecom.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );

    @Mapping(target = "images", ignore = true)
    ProductDTO productToProductDTO(Product product);
    @Mapping(target = "images", ignore = true)
    Product productDTOToProduct(ProductDTO productDTO);
}
