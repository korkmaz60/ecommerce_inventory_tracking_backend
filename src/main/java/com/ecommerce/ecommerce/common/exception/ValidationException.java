package com.ecommerce.ecommerce.common.exception;

/**
 * İş kuralı validation hataları için exception
 * Örnek: Şifre eşleşmeme, geçersiz format vb.
 */
public class ValidationException extends RuntimeException {

    private final String field;
    private final Object rejectedValue;

    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
    }

    public ValidationException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}
