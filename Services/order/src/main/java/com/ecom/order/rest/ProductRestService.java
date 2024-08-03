package com.ecom.order.rest;

import com.ecom.order.constants.APIEndPoints;
import com.ecom.order.model.Product;
import com.ecom.shared.common.exception.EcomException;
import com.ecom.shared.contract.dto.PageRequestDTO;
import com.ecom.shared.contract.dto.SearchCriteria;
import com.ecom.shared.contract.enums.Operator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProductRestService {

    @Value("${app.service.product.host}")
    private String host;

    @Autowired
    private WebClient webClient;

    UriComponentsBuilder getUriComponent(String context) {
        return UriComponentsBuilder.newInstance().scheme(HttpVersion.HTTP).host(host).port("8080").path(context);
    }

    public List<Product> getProducts(List<String> productIds) {
        List<Product> products = null;
        try {
           List<SearchCriteria> searchCriteria =  productIds.stream().map(productId-> SearchCriteria.builder().operator(Operator.EQUAL).key("_id").value(productId).build()).collect(Collectors.toList());
           PageRequestDTO pageRequestDTO = PageRequestDTO.builder().size(2).page(1).andCriteria(searchCriteria).build();
           products = webClient.method(HttpMethod.POST).uri(getUriComponent(APIEndPoints.PRODUCT_FILTER).build().toUri()).body(BodyInserters.fromValue(pageRequestDTO)).retrieve().bodyToMono(List.class).block();
        } catch (WebClientResponseException we) {
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_0004",we.getMessage());
        }
        return products;
    }


}
