package com.ecom.order.rest;

import com.ecom.order.constants.APIEndPoints;
import com.ecom.order.model.Product;
import com.ecom.shared.common.exception.EcomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@Slf4j
public class ProductRestService {

    @Value("${app.service.product.host}")
    private String host;

    @Autowired
    private WebClient webClient;

    UriComponentsBuilder getUriComponent(String context) {
        return UriComponentsBuilder.newInstance().scheme(Elements.HTTP).host(host).port("8080").path(context);
    }

    public List<Product> getProducts(List<String> productIds) {
        List<Product> products = null;
        try {
            products = webClient.method(HttpMethod.POST).uri(getUriComponent(APIEndPoints.PRODUCT_BASE_URL).build().toUri()).body(BodyInserters.fromValue(productIds)).retrieve().bodyToMono(List.class).block();
        } catch (WebClientResponseException we) {
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_0004");
        }
        return products;
    }


}
