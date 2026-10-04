package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class ProductVariantAlreadyExistException extends RuntimeException {
    public ProductVariantAlreadyExistException() {
        this("ProductVariant already exist in the list!");
    }

    public ProductVariantAlreadyExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
