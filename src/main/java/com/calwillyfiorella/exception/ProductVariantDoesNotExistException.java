package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class ProductVariantDoesNotExistException extends RuntimeException {
    public ProductVariantDoesNotExistException() {
        this("ProductVariant does not exist or invalid credentials!");
    }

    public ProductVariantDoesNotExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
