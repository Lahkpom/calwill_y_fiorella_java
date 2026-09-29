package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class ColorAlreadyExistException extends RuntimeException {
    public ColorAlreadyExistException() {
        this("Color already exist in the list!");
    }

    public ColorAlreadyExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
