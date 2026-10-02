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

    /**
     * Hace que el usuario seleccione por consola la opción de una RowStatus y la devuelve.
     *
     * @return RowStatus seleccionado por el usuario o un IllegalStateException
     */
    public static RowStatus readRowStatus(String prompt, boolean isStrict) {
        MenuHelper.printMenuTitle("OPCIONES DISPONIBLES:");
        // Lo hago hardcode porque esto no cambia
        System.out.format("""
                Opción 1. Activo.
                Opción 2. Inactivo.
                Opción 3. Eliminado.
                """);

        Integer newStatus = AuxiliarFunction.requireUserOption(List.of(1, 2, 3), prompt, isStrict);

        if (newStatus == null) return null;

        return switch (newStatus) {
            case 1 -> RowStatus.ACTIVE;
            case 2 -> RowStatus.INACTIVE;
            case 3 -> RowStatus.DELETED;
            default -> throw new IllegalStateException("Unexpected value: " + newStatus);
        };
    }

    /**
     * Lista los talles numéricos disponibles, solicita la selección del usuario y retorna la constante de NumericSize elegida.
     *
     * @return NumericSize seleccionado por el usuario.
     */
    public static NumericSize readNumericSize(String prompt, boolean isStrict) {
        MenuHelper.printMenuTitle("TALLES DISPONIBLES:");

        NumericSize[] sizes = NumericSize.values();

        for (int i = 0; i < sizes.length; i++) {
            // Elimina el prefijo "T_" y reemplaza "_" por "/" en talles dobles (ej: T_35_36 -> 35/36)
            String label = sizes[i].name()
                    .replaceFirst("^T_", "")
                    .replace("_", "/");

            System.out.format("Opción %d. %s%n", i + 1, label);
        }

        Integer selectedOption = AuxiliarFunction.requireUserOption(sizes.length, prompt, isStrict);

        if (selectedOption == null) return null;

        return sizes[selectedOption - 1];
    }

    /**
     * Lista los género objetivo disponibles, solicita la selección del usuario y retorna la constante de TargetGender elegida.
     *
     * @return TargetGender seleccionado por el usuario.
     */
    public static TargetGender readTargetGender(String prompt, boolean isStrict) {
        MenuHelper.printMenuTitle("GÉNEROS DISPONIBLES:");

        TargetGender[] genders = TargetGender.values();

        for (int i = 0; i < genders.length; i++) {
            System.out.format(
                    "Opción %d. %s%n",
                    i + 1,
                    ProductVariant.targetGenderDecode(genders[i])
            );
        }

        Integer selectedOption = AuxiliarFunction.requireUserOption(genders.length, prompt, isStrict);

        if (selectedOption == null) return null;

        return genders[selectedOption - 1];
    }
}
