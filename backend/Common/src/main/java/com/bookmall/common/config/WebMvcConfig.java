package com.bookmall.common.config;

import com.bookmall.common.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 * 只在Servlet容器环境（普通Web服务）中加载，Gateway（Reactive环境）不会加载
 *
 * 路径说明：
 * - Gateway 统一使用 StripPrefix=1，将 /api/xxx 转为 /xxx
 * - 服务内部拦截器匹配的是 Gateway 转发后的路径（不含 /api 前缀）
 * - 所以拦截器路径配置为 /**，白名单配置为 /xxx/** 格式
 *
 * @author BookMall Team
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // ✅ 修改：拦截所有请求（Gateway 已剥离 /api 前缀）
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // ============================================================
                        // 白名单路径（不需要 Token 验证）
                        // 注意：这些路径是 Gateway 转发后的内部路径，不含 /api 前缀
                        // 前端请求 /api/xxx → Gateway StripPrefix=1 → /xxx
                        // ============================================================

                        // ===== 用户认证接口 =====
                        "/user/login",
                        "/user/register",
                        "/user/captcha",
                        "/user/forgot-password",
                        "/user/send-code",
                        "/user/check-username",

                        // ===== 用户内部调用（Feign 调用，不需要 Token） =====
                        "/user/exists/**",
                        "/user/info/**",
                        "/user/balance/**",

                        // ===== 管理员认证接口 =====
                        "/admin/login",

                        // ===== 商品公开接口 =====
                        "/books/**",
                        "/categories/**",

                        // ===== 评价公开接口（列表/详情不需要登录） =====
                        "/review/book/**",
                        "/review/latest",
                        "/review/statistics/**",

                        // ===== 支付回调（外部系统调用） =====
                        "/payment/callback/**",

                        // ===== ✅ 购物车服务内部调用（Feign 调用，不需要 Token） =====
                        "/cart/**",

                        // ===== ✅ 地址服务内部调用（Feign 调用，不需要 Token） =====
                        "/address/**",

                        // ===== Swagger API 文档 =====
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**",

                        // ===== 其他公共资源 =====
                        "/favicon.ico",
                        "/error",
                        "/actuator/**",

                        // ===== Druid 监控 =====
                        "/druid/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}