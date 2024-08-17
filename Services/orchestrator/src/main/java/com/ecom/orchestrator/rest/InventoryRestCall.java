package com.ecom.orchestrator.rest;

import com.ecom.orchestrator.dto.InventoryResponse;
import com.ecom.shared.contract.dto.InventoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.apache.http.HttpVersion.HTTP;


@Component
public class InventoryRestCall {

    @Autowired
    @Qualifier(value = "inventory")
    private WebClient inventory;


    UriComponentsBuilder getUriComponent(String path) {
        return UriComponentsBuilder.newInstance().scheme(HTTP).host("localhost").port("8081").path(path);
    }


    public Mono<InventoryResponse> removeFromInventory(InventoryRequest inventoryRequest) {
        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        };
        UriComponents uriComponents = getUriComponent("inventory/remove").build();
        return inventory.put().uri(uriComponents.toUri()).headers(headers).body(BodyInserters.fromValue(inventoryRequest)).retrieve().
                bodyToMono(InventoryResponse.class);
    }

    public Mono<InventoryResponse> addToInventory(InventoryRequest inventoryRequest) {
        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        };
        UriComponents uriComponents = getUriComponent("inventory/add").build();
        return inventory.put().uri(uriComponents.toUri()).headers(headers).body(BodyInserters.fromValue(inventoryRequest)).retrieve().
                bodyToMono(InventoryResponse.class);
    }


}
