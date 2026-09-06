package com.bookmall.common.entity;

import com.bookmall.common.constant.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一响应结果")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码", example = "200")
    private int code;

    @Schema(description = "提示信息", example = "操作成功")
    private String message;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "时间戳")
    private long timestamp = System.currentTimeMillis();

    // ========== 成功响应 ==========
    public static <T> Result<T> success() {
        return new Result<>(StatusCode.SUCCESS, "操作成功", null, System.currentTimeMillis());
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(StatusCode.SUCCESS, message, null, System.currentTimeMillis());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(StatusCode.SUCCESS, "操作成功", data, System.currentTimeMillis());
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(StatusCode.SUCCESS, message, data, System.currentTimeMillis());
    }

    public static <T> Result<T> success(int code, String message, T data) {
        return new Result<>(code, message, data, System.currentTimeMillis());
    }

    // ========== 失败响应 ==========
    public static <T> Result<T> error() {
        return new Result<>(StatusCode.INTERNAL_SERVER_ERROR, "操作失败", null, System.currentTimeMillis());
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(StatusCode.INTERNAL_SERVER_ERROR, message, null, System.currentTimeMillis());
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data, System.currentTimeMillis());
    }

    // ========== 快捷错误方法 ==========
    public static <T> Result<T> badRequest(String message) {
        return error(StatusCode.BAD_REQUEST, message);
    }

    public static <T> Result<T> unauthorized(String message) {
        return error(StatusCode.UNAUTHORIZED, message);
    }

    public static <T> Result<T> forbidden(String message) {
        return error(StatusCode.FORBIDDEN, message);
    }

    public static <T> Result<T> notFound(String message) {
        return error(StatusCode.NOT_FOUND, message);
    }

    public static <T> Result<T> businessError(String message) {
        return error(StatusCode.BUSINESS_ERROR, message);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == StatusCode.SUCCESS;
    }
}