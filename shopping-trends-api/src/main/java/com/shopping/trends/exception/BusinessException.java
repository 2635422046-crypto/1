package com.shopping.trends.exception;

/**
 * 业务异常：用于在 Service / Controller 层主动抛出可预期的业务错误。
 * 继承 {@link RuntimeException}，无需在方法签名上声明，
 * 由 {@link GlobalExceptionHandler} 统一捕获并返回标准 Result 响应。
 */
public class BusinessException extends RuntimeException {

    /**
     * @param message 业务错误描述，会直接展示给前端
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * @param message 业务错误描述
     * @param cause   原始异常，保留堆栈以便日志追踪
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}