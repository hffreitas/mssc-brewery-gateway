package org.example.msscbrewerygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery")
@Configuration
public class LoadBalanceRoutesConfig {
    @Bean
    public RouteLocator loadBalanceRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/beer*", "/api/v1/beer/*", "/api/v1/beerUpc/*")
                        .uri("lb://beer-service"))
                .route(r -> r.path("/api/v1/customers/**", "/api/v1/customers*")
                        .uri("lb://order-beer-service"))
                .route(r -> r.path("/api/v1/beer/*/inventory")
                        .filters(f -> f.circuitBreaker(c -> c.setName("inventoryCB")
                                .setFallbackUri("forward:/inventory-failover")
                                .setRouteId("inv-failover")))
                        .uri("lb://inventory-service"))
                .route(r -> r.path("/inventory-failover/**")
                        .uri("lb://inventory-failover"))
                .build();
    }
}