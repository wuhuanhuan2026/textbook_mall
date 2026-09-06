package com.bookmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 认证配置属性
 *
 * @author BookMall Team
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * JWT密钥
     */
    private String secret = "book_mall_secret_key_2024_secure";

    /**
     * Token过期时间（毫秒）
     */
    private Long expiration = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Token请求头
     */
    private String header = "Authorization";

    /**
     * Token前缀
     */
    private String prefix = "Bearer ";
}