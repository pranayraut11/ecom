package com.ecom.order.mapper;

import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderProductInput;
import com.ecom.order.entity.Order;
import com.ecom.order.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mappings({
        @Mapping(target = "orderId", ignore = true),
        @Mapping(target = "userId", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "totalAmount", ignore = true)
    })
    Order toOrder(CreateOrderDTO dto, List<Product> products);

    Product toProduct(OrderProductInput input);
    List<Product> toProductList(List<OrderProductInput> inputs);
}
