package com.bookmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单配置属性
 *
 * @author BookMall Team
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth")  // 修改为 auth
public class WhitelistProperties {

    /**
     * 白名单URL列表（支持Ant风格通配符）
     */
    private List<String> whitelist = new ArrayList<>();

    /**
     * 获取白名单URL列表（兼容方法）
     */
    public List<String> getUrls() {
        if (whitelist == null || whitelist.isEmpty()) {
            return getDefaultWhitelist();
        }
        return whitelist;
    }

    /**
     * 默认白名单（当配置为空时使用）
     */
    public List<String> getDefaultWhitelist() {
        List<String> defaultList = new ArrayList<>();
        // 用户认证
        defaultList.add("/api/user/login");
        defaultList.add("/api/user/register");
        defaultList.add("/api/user/captcha");
        defaultList.add("/api/user/forgot-password");
        defaultList.add("/api/user/send-code");
        defaultList.add("/api/user/check-username");
        // 管理员认证
        defaultList.add("/api/admin/login");
        // 公开接口
        defaultList.add("/api/books");
        defaultList.add("/api/books/");
        defaultList.add("/api/books/**");
        defaultList.add("/api/categories");
        defaultList.add("/api/categories/");
        defaultList.add("/api/categories/**");
        defaultList.add("/api/review/list");
        defaultList.add("/api/review/list/**");
        defaultList.add("/api/review/detail/**");
        // 支付回调
        defaultList.add("/api/payment/callback/**");
        // Swagger
        defaultList.add("/swagger-ui/**");
        defaultList.add("/v3/api-docs/**");
        defaultList.add("/doc.html");
        defaultList.add("/webjars/**");
        // Actuator
        defaultList.add("/actuator/**");
        // 其他
        defaultList.add("/favicon.ico");
        defaultList.add("/error");
        return defaultList;
    }
}