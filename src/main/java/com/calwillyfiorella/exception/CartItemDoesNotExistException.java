package com.calwillyfiorella.exception;

import com.calwillyfiorella.util.ValidationUtils;

public class CartItemDoesNotExistException extends RuntimeException {
    public CartItemDoesNotExistException() {
        this("Item does not exist in the cart or invalid credentials!");
    }

    public CartItemDoesNotExistException(String message) {
        super(ValidationUtils.requireNonBlank(message, "El mensaje no puede estar vacío!"));
    }
}
