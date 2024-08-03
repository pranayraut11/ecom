package com.ecom.server.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    public static final String SEGMENT = "/${segment}";

    @Bean
    public RouteLocator routerFunction(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes().route(p -> p.path("/ecom/cart/**").
                        filters(f -> f.rewritePath("/ecom/cart/(?<segment>.*)", SEGMENT)).uri("lb://CART")).
                route(p -> p.path("/ecom/order/**").
                        filters(f -> f.rewritePath("/ecom/order/(?<segment>.*)", SEGMENT)).uri("lb://ORDER")).
                route(p -> p.path("/ecom/user/**").
                        filters(f -> f.rewritePath("/ecom/user/(?<segment>.*)", SEGMENT)).uri("lb://USER")).
                route(p -> p.path("/ecom/inventory/**").
                        filters(f -> f.rewritePath("/ecom/inventory/(?<segment>.*)", SEGMENT)).uri("lb://INVENTORY")).
                route(p -> p.path("/ecom/filemanager/**").
                        filters(f -> f.rewritePath("/ecom/filemanager/(?<segment>.*)", SEGMENT)).uri("lb://FILEMANAGER")).
                route(p -> p.path("/ecom/product/**").
                        filters(f -> f.rewritePath("/ecom/product/(?<segment>.*)", SEGMENT)).uri("lb://PRODUCT")).
                route(p -> p.path("/ecom/orchestrator/**").
                        filters(f -> f.rewritePath("/ecom/orchestrator/(?<segment>.*)", SEGMENT)).uri("lb://ORCHESTRATOR")).
                route(p -> p.path("/ecom/payment/**").
                        filters(f -> f.rewritePath("/ecom/payment/(?<segment>.*)", SEGMENT)).uri("lb://PAYMENT")).
                build();
    }
}
