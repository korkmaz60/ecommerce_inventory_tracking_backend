package com.ecommerce.ecommerce.common.exception;

/**
 * Yetkisiz erişim denemelerinde fırlatılan exception
 * Örnek: Geçersiz token, expired token vb.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
