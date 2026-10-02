package com.calwillyfiorella.util;

import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.ui.menuUtils.MenuHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public final class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    private InputUtils() {}

    public static void closeScanner() {
        scanner.close();
    }

    public static Integer readInt(String prompt, boolean isStrict) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            if (!isStrict && input.isBlank()) return null;

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println("Debes ingresar un número entero válido.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.err.println("Debes ingresar un número con decimales válido (Pruebe con punto o con coma para separar decimales).");
            }
        }
    }

    public static BigDecimal readPrice(String prompt, boolean isStrict) {
        System.out.println("¡¡¡ Solo ingrese un punto o coma para separar decimales. !!!");
        while (true) {
            String input = readString(prompt, isStrict).replace(",", ".");

            try {
                if (!isStrict && input.isBlank()) return null;

                if (input.isBlank())
                    throw new IllegalArgumentException("El valor ingresado no es válido.");

                // Verificamos si existe más de un punto en la cadena
                if (input.indexOf('.') != input.lastIndexOf('.'))
                    throw new IllegalArgumentException("El monto no puede contener más de un separador decimal (punto/coma).");

                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(BigDecimal.ZERO) < 0)
                    throw new IllegalArgumentException("El precio no puede ser negativo.");

                return value.setScale(2, java.math.RoundingMode.HALF_UP);

            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.err.println("Debe ingresar un monto monetario válido (ejemplo: 12500 o 12500.50).");
            }
        }
    }

    public static String readString(String prompt, boolean isStrict) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            if (!isStrict && input.isBlank()) return null;

            if (input.isBlank()) {
                System.err.println("No puede ingresar un texto vacío.");
                continue;
            }

            return input;
        }
    }




}
