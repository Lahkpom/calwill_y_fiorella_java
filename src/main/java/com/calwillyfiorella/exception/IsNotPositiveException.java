package com.calwillyfiorella.exception;

public class IsNotPositiveException extends RuntimeException {
    public IsNotPositiveException(String message) {
        super((message != null && !message.isBlank()) ? message : "La cantidad debe ser mayor a cero.");
    }
}
