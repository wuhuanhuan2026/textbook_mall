package com.bookmall.admin.interceptor;

import com.bookmall.common.constant.Constants;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.JwtUtil;
import com.bookmall.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员认证拦截器
 *
 * @author BookMall Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final RedisUtil redisUtil;

    // Redis Key前缀
    private static final String REDIS_ADMIN_TOKEN_KEY = "admin:token:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(Constants.TOKEN_HEADER);

        // 检查Token是否存在
        if (!StringUtils.hasText(header)) {
            log.warn("管理员请求缺少Token: {}", request.getRequestURI());
            throw new BusinessException(ExceptionEnum.LOGIN_REQUIRED);
        }

        // 去掉Bearer前缀
        String token = header;
        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }

        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            log.warn("管理员Token无效或已过期: {}", token);
            throw new BusinessException(ExceptionEnum.TOKEN_INVALID);
        }

        // 从Token获取管理员信息
        Long adminId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        // 验证Redis中的Token
        String cacheKey = REDIS_ADMIN_TOKEN_KEY + adminId;
        Object cachedToken = redisUtil.get(cacheKey);
        if (cachedToken == null || !cachedToken.equals(token)) {
            log.warn("管理员Token已失效: adminId={}", adminId);
            throw new BusinessException(ExceptionEnum.TOKEN_INVALID);
        }

        // 存入请求上下文
        request.setAttribute(Constants.REQ_ATTR_USER_ID, adminId);
        request.setAttribute(Constants.REQ_ATTR_USERNAME, username);
        request.setAttribute(Constants.REQ_ATTR_ROLE, role);

        log.debug("管理员认证通过: adminId={}, username={}, role={}", adminId, username, role);
        return true;
    }
}