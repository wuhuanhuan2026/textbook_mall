package com.bookmall.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局异常处理器
 *
 * @author BookMall Team
 */
@Slf4j
@Order(-1)
@Configuration
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(getHttpStatus(ex));

        String errorMsg = getErrorMessage(ex);
        String jsonResponse = String.format(
                "{\"code\":%d,\"message\":\"%s\",\"timestamp\":%d}",
                response.getStatusCode().value(),
                errorMsg,
                System.currentTimeMillis()
        );

        DataBuffer buffer = bufferFactory.wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 获取HTTP状态码
     */
    private HttpStatus getHttpStatus(Throwable ex) {
        if (ex instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException) ex).getStatus();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 获取错误信息
     */
    private String getErrorMessage(Throwable ex) {
        if (ex instanceof NotFoundException) {
            return "服务不存在";
        }
        if (ex instanceof ResponseStatusException) {
            return ex.getMessage();
        }
        log.error("网关异常: ", ex);
        return "系统繁忙，请稍后再试";
    }
}