package com.hyw.redis.service.exception;
/**
 * 异常类
 */
public class UnableToAquireLockException extends RuntimeException {

    public UnableToAquireLockException() {
    }

    public UnableToAquireLockException(String message) {
        super(message);
    }

    public UnableToAquireLockException(String message, Throwable cause) {
        super(message, cause);
    }
}