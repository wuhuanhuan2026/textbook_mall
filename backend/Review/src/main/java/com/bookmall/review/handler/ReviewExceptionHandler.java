package com.bookmall.review.handler;

import com.bookmall.common.entity.Result;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 评价模块异常处理器
 *
 * @author BookMall Team
 */
@Slf4j
@RestControllerAdvice
public class ReviewExceptionHandler {

    /**
     * Feign调用异常
     */
    @ExceptionHandler(FeignException.class)
    public Result<Void> handleFeignException(FeignException e) {
        log.error("Feign调用失败: status={}, message={}", e.status(), e.getMessage());
        if (e.status() == 404) {
            return Result.error("服务不可用，请稍后再试");
        }
        if (e.status() >= 500) {
            return Result.error("服务异常，请稍后再试");
        }
        return Result.error("请求失败，请稍后再试");
    }

    /**
     * 业务异常
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