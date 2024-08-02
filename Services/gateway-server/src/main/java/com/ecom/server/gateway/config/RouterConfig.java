package com.ecom.server.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {
    @Bean
    public RouteLocator routerFunction(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes().route(p->p.path("/ecom/cart/**").
                filters(f->f.rewritePath("/ecom/cart/(?<segment>.*)","/${segment}")).uri("lb://CART-SERVICE")).build();
    }
}
