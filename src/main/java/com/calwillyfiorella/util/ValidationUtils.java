package com.calwillyfiorella.util;

import java.math.BigDecimal;
import java.util.Objects;

public final class ValidationUtils {
    // Clase final para evitar herencia con extends. Constructor privado para bloquear new ValidationUtils()
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(message);
        return value;
    }

    public static Integer requireNonNegative(Integer value, String message) {
        if (value == null || value <= 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    public static BigDecimal requireAmountGreaterThanZero(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(message);
        return value;
    }

    // Para Stock (Integer)
    public static Integer requireSufficientQuantity(Integer currentQuantity, Integer quantityToDeduct, String message) {
        Objects.requireNonNull(currentQuantity, "Current quantity cannot be null");
        Objects.requireNonNull(quantityToDeduct, "Quantity to deduct cannot be null");

        if (quantityToDeduct <= 0 || (currentQuantity - quantityToDeduct) < 0)
            throw new IllegalArgumentException(message);

        return quantityToDeduct;
    }

    // Para Precios / Dinero (BigDecimal)
    public static BigDecimal requireSufficientAmount(BigDecimal currentAmount, BigDecimal amountToDeduct, String message) {
        Objects.requireNonNull(currentAmount, "Current amount cannot be null");
        Objects.requireNonNull(amountToDeduct, "Amount to deduct cannot be null");

        if (
                amountToDeduct.compareTo(BigDecimal.ZERO) <= 0
                ||
                currentAmount.subtract(amountToDeduct).compareTo(BigDecimal.ZERO) < 0
        ) throw new IllegalArgumentException(message);

        return amountToDeduct;
    }

    public static Integer requireValidIntegerIdBySeq(Integer currVal, Integer newVal) {
        if (currVal == null || newVal == null || newVal != currVal - 1)
            throw new IllegalArgumentException("El id ingresado no coincide con la secuencia. Valor actual: " + currVal);
        return newVal;
    }
}