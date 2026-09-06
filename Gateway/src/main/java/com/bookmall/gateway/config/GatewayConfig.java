package com.bookmall.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关路由配置
 *
 * @author BookMall Team
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User服务路由
                .route("user-service", r -> r
                        .path("/api/user/**")
                        .filters(f -> f
                                .stripPrefix(1)  // 移除 /api 前缀
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8081"))

                // Admin服务路由
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8087"))

                // Book服务路由
                .route("book-service", r -> r
                        .path("/api/books/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8083"))

                // Category服务路由
                .route("category-service", r -> r
                        .path("/api/categories/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8082"))

                // Cart服务路由
                .route("cart-service", r -> r
                        .path("/api/cart/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8084"))

                // Order服务路由
                .route("order-service", r -> r
                        .path("/api/order/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8085"))

                // Payment服务路由
                .route("payment-service", r -> r
                        .path("/api/payment/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8086"))

                // Review服务路由
                .route("review-service", r -> r
                        .path("/api/review/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8089"))

                // Address服务路由
                .route("address-service", r -> r
                        .path("/api/address/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(config -> config.setRetries(3)))
                        .uri("http://localhost:8088"))
                .build();
    }
}