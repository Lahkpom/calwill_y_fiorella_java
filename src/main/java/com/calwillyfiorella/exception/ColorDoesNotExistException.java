package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class ColorDoesNotExistException extends RuntimeException {
    public ColorDoesNotExistException() {
        this("Color does not exist or invalid credentials!");
    }

    public ColorDoesNotExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
