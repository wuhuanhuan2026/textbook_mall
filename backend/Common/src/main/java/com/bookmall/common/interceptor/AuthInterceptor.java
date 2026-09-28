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
 *
 * 路径说明：
 * - Book 服务的 Controller 映射为 @RequestMapping("/books")
 * - 前端经 Gateway 访问时，Gateway StripPrefix=1 会去掉 /api，Book 服务收到 /books/**
 * - Feign 内部调用时，直接请求 /books/**
 * - 因此白名单要同时兼容 /books/** 和 /api/books/** 两种形式
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 白名单路径 - 不需要Token验证
     * 注意：以 "/" 结尾的为前缀匹配，其余为精确匹配
     */
    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            // ===== 静态资源（图片、样式、脚本等） =====
            "/images/",
            "/css/",
            "/js/",
            "/fonts/",
            "/static/",
            "/public/",

            // ===== 公开测试接口 =====
            "/api/user/test",
            "/api/user/status",
            "/api/user/welcome",
            "/api/user/endpoints",

            // ===== 认证接口（登录/注册不需要Token） =====
            "/api/user/login",
            "/api/user/register",
            "/api/user/check-username",

            // ===== Book 公开接口（浏览商品不需要登录） =====
            // 兼容两种前缀：/books/**（Book 服务真实路径）和 /api/books/**（Gateway 未 StripPrefix 时）
            "/books",
            "/books/",
            "/books/hot",
            "/books/new",
            "/books/page",
            "/books/detail/",
            "/books/search",
            "/books/category/",

            "/api/books",
            "/api/books/",
            "/api/books/hot",
            "/api/books/new",
            "/api/books/page",
            "/api/books/detail/",
            "/api/books/search",
            "/api/books/category/",

            // 分类接口
            "/categories/list",
            "/api/categories/list",

            // ===== Swagger API文档 =====
            "/swagger-ui.html",
            "/swagger-ui/",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/doc.html",
            "/webjars/",

            // ===== Druid监控 =====
            "/druid",
            "/druid/",

            // ===== 其他公共资源 =====
            "/favicon.ico",
            "/error",
            "/actuator/health",
            "/actuator/info"
    ));

    /**
     * 图书库存/上架/价格等带动态ID的公开查询接口
     * 同时兼容带 /api 和不带 /api 两种形式：
     *   - /books/3/stock
     *   - /api/books/3/stock
     */
    private static final String BOOK_DYNAMIC_PATH_REGEX =
            "^(/api)?/books/\\d+/(stock|on-sale|price)$";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        log.debug("拦截请求: {} {}", request.getMethod(), path);

        // 1. 检查是否在白名单中（精确匹配、前缀匹配、正则匹配）
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
     * 支持三种匹配方式：
     * 1. 精确匹配（如 /books/hot）
     * 2. 前缀匹配（以 "/" 结尾的白名单项，如 /images/）
     * 3. 正则匹配（针对带动态ID的公开接口，如 /books/3/stock）
     */
    private boolean isWhiteListed(String path) {
        // 1. 精确匹配
        if (WHITE_LIST.contains(path)) {
            return true;
        }

        // 2. 前缀匹配（白名单项以 "/" 结尾）
        for (String whitePath : WHITE_LIST) {
            if (whitePath.endsWith("/") && path.startsWith(whitePath)) {
                return true;
            }
        }

        // 3. 正则匹配：/books/{id}/stock、/books/{id}/on-sale、/books/{id}/price
        //    同时兼容 /api/books/{id}/... 形式
        if (path.matches(BOOK_DYNAMIC_PATH_REGEX)) {
            return true;
        }

        return false;
    }
}