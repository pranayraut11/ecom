package com.ecom.shared.common.config.security;

import com.ecom.shared.common.dto.UserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class HttpRequestFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest httpServletRequest =  exchange.getRequest();
        String authToken = httpServletRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasLength(authToken)){
            try {
                UserDetails.setUserInfo(authToken.replaceFirst(OAuth2AccessToken.TokenType.BEARER.getValue(),"").trim());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return  chain.filter(exchange);
    }
}
