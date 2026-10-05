package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        this("La contraseña ingresada fue incorrecta!");
    }

    public WrongPasswordException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
