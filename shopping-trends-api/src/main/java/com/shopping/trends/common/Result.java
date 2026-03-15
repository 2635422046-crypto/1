package com.shopping.trends.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应结果类
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 构造方法私有化，禁止直接创建
     */
    private Result() {
    }

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    private Result(Integer code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, true);
    }

    /**
     * 成功返回结果
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     数据类型
     * @return 结果
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data, true);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param <T>       数据类型
     * @return 结果
     */
    public static <T> Result<T> failed(IErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null, false);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param <T>       数据类型
     * @return 结果
     */
    public static <T> Result<T> failed(IErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 结果
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCode.FAILED.getCode(), message, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param <T> 数据类型
     * @return 结果
     */
    public static <T> Result<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param <T> 数据类型
     * @return 结果
     */
    public static <T> Result<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 结果
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), message, null, false);
    }

    /**
     * 未登录返回结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 结果
     */
    public static <T> Result<T> unauthorized(T data) {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data, false);
    }

    /**
     * 未授权返回结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 结果
     */
    public static <T> Result<T> forbidden(T data) {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data, false);
    }
}