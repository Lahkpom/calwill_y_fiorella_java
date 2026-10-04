package com.calwillyfiorella.util;

import com.calwillyfiorella.exception.IsNotPositiveException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private ValidationUtils() {}

    public static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(message);
        return value;
    }

    public static Integer requireNonNegative(Integer value, String message) {
        return requireNonNegative(value, message, false);
    }

    public static Integer requireNonNegative(Integer value, String message, boolean strict) {
        if (value == null || value < 0 || (strict && value <= 0))
            throw new IsNotPositiveException(message);
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
        if (currVal == null || newVal == null || currVal != newVal - 1)
            throw new IllegalArgumentException("El id ingresado no coincide con la secuencia. Valor actual: " + currVal);
        return newVal;
    }

    /**
     * Limpia cualquier carácter no numérico y verifica que contenga exactamente 10 dígitos.
     *
     * @param rawPhone Cadena de texto recibida (ej: "(11) 2345-6789", "11 2345 6789", "11-2345-6789")
     * @return El número normalizado con solo sus 10 dígitos numéricos.
     * @throws IllegalArgumentException si es nulo o si no tiene exactamente 10 dígitos.
     */
    public static String validateAndGetPhone(String rawPhone) {
        if (rawPhone == null)
            throw new IllegalArgumentException("El número de teléfono no puede ser nulo.");

        // Remueve lo que NO sea un dígito numérico (0-9)
        String digitsOnly = rawPhone.replaceAll("[^0-9]", "");

        // Valida que tenga exactamente 10 dígitos
        if (!digitsOnly.matches("^\\d{10}$"))
            throw new IllegalArgumentException("El teléfono debe contener exactamente 10 dígitos numéricos.");

        return digitsOnly;
    }

    /**
     * Valida si una cadena de texto cumple con el formato estándar de correo electrónico.
     *
     * @param email Texto a evaluar.
     * @return true si tiene formato válido, false en caso contrario o si es nulo.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida el email y lanza IllegalArgumentException si no cumple el formato.
     * Devuelve el email limpio (trim y minúsculas).
     */
    public static String requireValidEmail(String email) {
        if (!isValidEmail(email))
            throw new IllegalArgumentException("El formato del correo electrónico ingresado no es válido.");

        return email.trim().toLowerCase();
    }
}