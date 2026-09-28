package com.bookmall.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign 配置 - 透传认证信息
 *
 * 作用：
 * 微服务之间通过 Feign 调用时，默认不会携带调用方的请求头。
 * 这会导致下游服务的 AuthInterceptor 读取不到 Token，报"请先登录"。
 *
 * 本配置创建一个 Feign 的 RequestInterceptor，
 * 在每次 Feign 调用前，把当前请求的认证信息（X-User-Id、Authorization 等）
 * 透传到 Feign 请求头中，让下游服务能正确识别用户身份。
 *
 * 注意事项：
 * 1. 只在 Servlet Web 环境生效（Gateway 是 WebFlux，不会加载）
 * 2. 使用 @ConditionalOnClass 避免在非 Feign 环境报错
 *
 * @author BookMall Team
 */
@Slf4j
@Configuration
@ConditionalOnClass(RequestInterceptor.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class FeignConfig {

    /**
     * Feign 请求拦截器 - 透传认证信息
     */
    @Bean
    public RequestInterceptor feignTokenInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                // 非 Web 请求上下文（如定时任务、异步线程），不处理
                if (attributes == null) {
                    return;
                }

                HttpServletRequest request = attributes.getRequest();

                // 1. 透传网关解析后的用户信息（下游服务的 AuthInterceptor 优先读这些）
                String userId = request.getHeader("X-User-Id");
                String username = request.getHeader("X-Username");
                String role = request.getHeader("X-User-Role");

                if (userId != null && !userId.isEmpty()) {
                    template.header("X-User-Id", userId);
                }
                if (username != null && !username.isEmpty()) {
                    template.header("X-Username", username);
                }
                if (role != null && !role.isEmpty()) {
                    template.header("X-User-Role", role);
                }

                // 2. 透传原始 Authorization 头（兼容只认 Authorization 的下游服务）
                String authorization = request.getHeader("Authorization");
                if (authorization != null && !authorization.isEmpty()) {
                    template.header("Authorization", authorization);
                }

                // 3. 透传链路追踪 ID（便于排查问题）
                String traceId = request.getHeader("X-Trace-Id");
                if (traceId != null && !traceId.isEmpty()) {
                    template.header("X-Trace-Id", traceId);
                }

                log.debug("Feign 透传认证信息: url={}, userId={}, hasAuth={}",
                        template.url(), userId, authorization != null);
            }
        };
    }
}