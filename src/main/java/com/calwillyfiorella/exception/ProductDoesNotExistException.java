package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class ProductDoesNotExistException extends RuntimeException {
    public ProductDoesNotExistException() {
        this("Prodcut does not exist or invalid credentials!");
    }

    public ProductDoesNotExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
