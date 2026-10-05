package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class SaleAlreadyExistException extends RuntimeException {
    public SaleAlreadyExistException() {
        this("Sale already exist in the list!");
    }

    public SaleAlreadyExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
