package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class FailedLoginAttemptException extends RuntimeException {
    public FailedLoginAttemptException() {
        this("Falló el intento de log in!");
    }

    public FailedLoginAttemptException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }

    public FailedLoginAttemptException(String message, Throwable cause) {
        super(message, cause);
    }
}
