package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class ProductAlreadyExistException extends RuntimeException {
    public ProductAlreadyExistException() {
        this("Product already exist in the list!");
    }

    public ProductAlreadyExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
