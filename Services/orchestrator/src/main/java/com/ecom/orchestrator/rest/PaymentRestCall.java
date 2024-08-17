package com.ecom.orchestrator.rest;

import com.ecom.orchestrator.dto.PaymentResponse;
import com.ecom.shared.contract.dto.PaymentRequest;
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
public class PaymentRestCall {

    @Autowired
    @Qualifier(value = "payment")
    private WebClient webClient;

    UriComponentsBuilder getUriComponents() {
        return UriComponentsBuilder.newInstance().scheme(HTTP).host("localhost").port(8082).path("payment/pay");

    }

    public Mono<PaymentResponse> doPayment(PaymentRequest paymentRequest) {
        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        };
        UriComponents uriComponents = this.getUriComponents().build();
        return webClient.post().uri(uriComponents.toUri()).headers(headers).body(BodyInserters.fromValue(paymentRequest)).retrieve().bodyToMono(PaymentResponse.class);
    }

}
