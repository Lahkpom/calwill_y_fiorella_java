package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class SaleDoesNotExistException extends RuntimeException {
    public SaleDoesNotExistException() {
        this("Sale does not exist or invalid credentials!");
    }

    public SaleDoesNotExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
