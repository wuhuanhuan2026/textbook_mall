package com.bookmall.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private final int code;
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = 1001;
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }

    public BusinessException(ExceptionEnum exceptionEnum, String extraMessage) {
        super(exceptionEnum.getMessage() + ": " + extraMessage);
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage() + ": " + extraMessage;
    }
}