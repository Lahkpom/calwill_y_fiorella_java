package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class IsNotAnAdminException extends RuntimeException {
    public IsNotAnAdminException() {
        this("Esta acción solo la pueden realizar usuarios Administradores.");
    }

    public IsNotAnAdminException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
