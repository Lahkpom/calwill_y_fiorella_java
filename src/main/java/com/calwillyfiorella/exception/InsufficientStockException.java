package com.calwillyfiorella.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super((message != null && !message.isBlank()) ? message : "La cantidad ingresada supera el stock disponible.");
    }
}
