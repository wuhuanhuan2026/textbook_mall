package com.bookmall.cart.handler;

import com.bookmall.common.entity.Result;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

/**
 * 购物车模块异常处理器
 *
 * @author BookMall Team
 */
@Slf4j
@RestControllerAdvice
public class CartExceptionHandler {

    /**
     * Feign调用异常处理
     */
    @ExceptionHandler(ConnectException.class)
    public Result<Void> handleConnectException(ConnectException e) {
        log.error("服务调用失败: {}", e.getMessage());
        return Result.error("服务暂时不可用，请稍后再试");
    }

    /**
     * 其他业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ExceptionEnum.SYSTEM_ERROR.getMessage());
    }
}