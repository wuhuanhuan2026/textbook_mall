package com.bookmall.gateway.filter;

import com.bookmall.gateway.properties.WhitelistProperties;
import com.bookmall.gateway.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 认证全局过滤器 - 验证JWT Token
 *
 * @author BookMall Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final WhitelistProperties whitelistProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        log.info("===== AuthGlobalFilter 拦截请求: {} =====", path);

        // 获取白名单列表
        List<String> whitelist = whitelistProperties.getUrls();
        log.info("当前白名单配置: {}", whitelist);

        if (whitelist != null && !whitelist.isEmpty()) {
            for (String pattern : whitelist) {
                if (pathMatcher.match(pattern, path)) {
                    log.info("✅ 白名单匹配成功，跳过认证: {} 匹配 {}", path, pattern);
                    return chain.filter(exchange);
                }
            }
        } else {
            log.warn("⚠️ 白名单配置为空，使用默认白名单");
            // 使用默认白名单
            List<String> defaultWhitelist = whitelistProperties.getDefaultWhitelist();
            for (String pattern : defaultWhitelist) {
                if (pathMatcher.match(pattern, path)) {
                    log.info("✅ 默认白名单匹配成功，跳过认证: {} 匹配 {}", path, pattern);
                    return chain.filter(exchange);
                }
            }
        }

        log.info("❌ 不在白名单中，需要认证: {}", path);

        // 获取Token
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            log.warn("❌ 请求缺少Token: {}", path);
            return unauthorizedResponse(exchange);
        }

        log.debug("Token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            log.warn("❌ Token无效或已过期: {}", path);
            return unauthorizedResponse(exchange);
        }

        // 从Token获取用户信息
        Long userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        if (userId == null) {
            log.warn("❌ Token解析失败: {}", path);
            return unauthorizedResponse(exchange);
        }

        // 将用户信息放入请求头，传递给下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Username", username != null ? username : "")
                .header("X-User-Role", role != null ? role : "")
                .build();

        log.info("✅ 认证通过: userId={}, username={}, role={}, path={}", userId, username, role, path);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}