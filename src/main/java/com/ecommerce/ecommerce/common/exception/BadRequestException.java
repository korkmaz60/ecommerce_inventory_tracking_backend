package com.ecommerce.ecommerce.common.exception;

/**
 * Geçersiz istek durumunda fırlatılan exception
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
