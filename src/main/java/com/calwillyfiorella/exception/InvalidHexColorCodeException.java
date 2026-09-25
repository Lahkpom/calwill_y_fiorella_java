package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class InvalidHexColorCodeException extends RuntimeException {
    public InvalidHexColorCodeException() {
        this("El formato del código debe ser #[6 caracteres alfanuméricos].");
    }

    public InvalidHexColorCodeException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
