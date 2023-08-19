package com.ecom.product.mappers.specification;

import com.ecom.product.dto.PriceDTO;
import com.ecom.product.entity.Price;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", implementationPackage = "com.ecom.product.mappers.specification")

public interface PriceMapper {

    PriceMapper PRICE_MAPPER = Mappers.getMapper(PriceMapper.class);

    PriceDTO priceToPriceDTO(Price price);

    Price priceDtoToPrice(PriceDTO priceDTO);
}
