package com.ecom.order.rest;


import com.ecom.order.constants.APIEndPoints;
import com.ecom.order.dto.Cart;
import com.ecom.shared.common.exception.EcomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class CartRestService {

    @Value("${app.service.cart.host}")
    private String host;

    @Autowired
    private WebClient webClient;

    UriComponentsBuilder getUriComponent(String context) {
        return UriComponentsBuilder.newInstance().scheme(HttpVersion.HTTP).host(host).port("8080").path(context);
    }

    public Cart getCart() {
        Cart cart = null;
        try {
            cart = webClient.method(HttpMethod.GET).uri(getUriComponent(APIEndPoints.CART_PRODUCT_URL).build().toUri()).retrieve().bodyToMono(Cart.class).block();
        } catch (WebClientResponseException we) {
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_0004");
        }
        return cart;
    }

    public void deleteCart(String id) {
        try {
            webClient.method(HttpMethod.DELETE).uri(getUriComponent(APIEndPoints.CART_BASE_URL).path("/"+id).build().toUri()).retrieve().bodyToMono(Void.class).block();
        } catch (WebClientResponseException we) {
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_0004");
        }
    }
}
