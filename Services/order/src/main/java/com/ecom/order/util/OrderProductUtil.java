package com.ecom.order.util;

import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderProductInput;
import com.ecom.order.model.Product;
import com.ecom.order.rest.ProductClient;
import com.ecom.shared.contract.dto.PageRequestDTO;
import com.ecom.shared.contract.dto.SearchCriteria;
import java.util.ArrayList;
import java.util.List;

public class OrderProductUtil {
    public static List<Product> fetchProductsForOrder(CreateOrderDTO createOrderDTO, ProductClient productClients) {
        List<String> productIds = createOrderDTO.getProducts().stream()
                .map(OrderProductInput::getProductId)
                .toList();
        List<SearchCriteria> inCriteria = new ArrayList<>();
        inCriteria.add(SearchCriteria.builder().key("_id").values(new ArrayList<>(productIds)).build());
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .inCriteria(inCriteria)
                .size(productIds.size())
                .page(1)
                .build();
        return productClients.getProducts(pageRequestDTO);
    }
}
