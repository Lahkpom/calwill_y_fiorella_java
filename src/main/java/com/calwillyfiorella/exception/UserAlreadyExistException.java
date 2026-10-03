package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException() {
        this("User already exist in the list!");
    }

    public UserAlreadyExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
