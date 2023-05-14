package com.ecom.order.rest;

import com.ecom.order.dto.InventoryDTO;
import com.ecom.shared.common.exception.EcomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class InventoryRestService {

    @Value("${app.service.inventory.host}")
    private String host;


    @Autowired
    private WebClient webClient;

    UriComponentsBuilder getUriComponent(String path) {
        return UriComponentsBuilder.newInstance().scheme(Elements.HTTP).host(host).port("8080").path(path);
    }

    public List<String> checkStockAvailability(List<InventoryDTO> inventoryDTO) {
        UriComponents uriComponents = getUriComponent("").build();
        try {
            return webClient.post().uri(uriComponents.toUri()).body(BodyInserters.fromValue(inventoryDTO)).retrieve().bodyToMono(List.class).block();
        } catch (WebClientResponseException we) {
            throw new EcomException(we.getStatusCode(), "INVN_0001", we.getMessage(), false);
        }
    }

}
