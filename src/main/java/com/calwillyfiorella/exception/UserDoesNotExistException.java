package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException() {
        this("User does not exist or invalid credentials!");
    }

    public UserDoesNotExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
