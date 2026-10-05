package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class UserLockedException extends RuntimeException {
    public UserLockedException() {
        this("La cuenta se encuentra bloqueada temporalmente!");
    }

    public UserLockedException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
