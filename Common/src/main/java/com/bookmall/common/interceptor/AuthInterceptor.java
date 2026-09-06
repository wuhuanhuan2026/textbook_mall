package com.bookmall.common.interceptor;

import com.bookmall.common.constant.Constants;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 认证拦截器
 * 只在Servlet容器环境（普通Web服务）中加载，Gateway（Reactive环境）不会加载
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 白名单路径 - 不需要Token验证
     */
    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            // ===== 公开测试接口 =====
            "/api/user/test",
            "/api/user/status",
            "/api/user/welcome",
            "/api/user/endpoints",

            // ===== 认证接口（登录/注册不需要Token） =====
            "/api/user/login",
            "/api/user/register",
            "/api/user/check-username",

            // ===== Swagger API文档 =====
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/",

            // ===== Druid监控 =====
            "/druid",
            "/druid/",

            // ===== 其他公共资源 =====
            "/favicon.ico",
            "/error"
    ));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        log.debug("拦截请求: {} {}", request.getMethod(), path);

        // 1. 检查是否在白名单中（精确匹配或前缀匹配）
        if (isWhiteListed(path)) {
            log.debug("白名单路径，放行: {}", path);
            return true;
        }

        // 2. 检查Token
        String header = request.getHeader(Constants.TOKEN_HEADER);

        // 检查Token是否存在
        if (!StringUtils.hasText(header)) {
            log.warn("请求缺少Token: {}", request.getRequestURI());
            throw new BusinessException(ExceptionEnum.LOGIN_REQUIRED);
        }

        // 去掉Bearer前缀
        String token = header;
        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }

        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            log.warn("Token无效或已过期: {}", token);
            throw new BusinessException(ExceptionEnum.TOKEN_INVALID);
        }

        // 从Token获取用户信息并存入请求上下文
        Long userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        request.setAttribute(Constants.REQ_ATTR_USER_ID, userId);
        request.setAttribute(Constants.REQ_ATTR_USERNAME, username);
        request.setAttribute(Constants.REQ_ATTR_ROLE, role);

        log.debug("认证通过: userId={}, username={}, role={}", userId, username, role);
        return true;
    }

    /**
     * 判断路径是否在白名单中
     */
    private boolean isWhiteListed(String path) {
        // 精确匹配
        if (WHITE_LIST.contains(path)) {
            return true;
        }

        // 前缀匹配（处理 /swagger-ui/**, /v3/api-docs/** 等）
        for (String whitePath : WHITE_LIST) {
            if (whitePath.endsWith("/") && path.startsWith(whitePath)) {
                return true;
            }
        }

        return false;
    }
}