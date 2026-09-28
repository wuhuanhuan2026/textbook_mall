package com.bookmall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求追踪过滤器 - 添加请求追踪信息
 *
 * @author BookMall Team
 */
@Slf4j
@Component
public class RequestTraceFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 添加请求时间戳
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Request-Time", String.valueOf(System.currentTimeMillis()))
                        .build())
                .build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -150;
    }
}