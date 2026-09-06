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
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // ===== 用户认证 =====
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/captcha",
                        "/api/user/forgot-password",

                        // ===== 用户内部调用（不需要Token） =====
                        "/api/user/exists/**",
                        "/api/user/info/**",
                        "/api/user/balance/**",

                        // ===== 地址服务内部调用（不需要Token） =====
                        "/api/address/**",

                        // ===== ✅ 购物车服务内部调用（不需要Token） =====
                        "/api/cart/**",

                        // ===== 管理员登录 =====
                        "/api/admin/login",

                        // ===== 商品公开接口 =====
                        "/api/books/**",
                        "/api/categories/**",

                        // ===== Swagger =====
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**"
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