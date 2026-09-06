package com.bookmall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 日志全局过滤器 - 记录请求日志
 *
 * @author BookMall Team
 */
@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 声明为 final
        final String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        final long startTime = System.currentTimeMillis();

        // 获取请求信息
        final String method = exchange.getRequest().getMethod().name();
        final String path = exchange.getRequest().getPath().value();
        final String remoteAddr = exchange.getRequest().getRemoteAddress() != null ?
                exchange.getRequest().getRemoteAddress().getHostString() : "unknown";

        // 记录请求日志
        log.info("请求开始: traceId={}, method={}, path={}, remoteAddr={}",
                traceId, method, path, remoteAddr);

        // 将traceId放入请求头，传递给下游服务
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Trace-Id", traceId)
                        .build())
                .build();

        return chain.filter(mutatedExchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int status = mutatedExchange.getResponse().getStatusCode() != null ?
                            mutatedExchange.getResponse().getStatusCode().value() : 0;
                    log.info("请求完成: traceId={}, method={}, path={}, status={}, duration={}ms",
                            traceId, method, path, status, duration);
                });
    }

    @Override
    public int getOrder() {
        // 最高优先级，最先执行
        return -200;
    }
}