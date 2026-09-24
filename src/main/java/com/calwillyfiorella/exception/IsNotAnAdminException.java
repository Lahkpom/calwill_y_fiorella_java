package com.calwillyfiorella.exception;

public class IsNotAnAdminException extends RuntimeException {
    public IsNotAnAdminException(String message) {
        super((message != null && !message.isBlank()) ? message : "Esta acción solo la pueden realizar usuarios Administradores.");
    }
}
