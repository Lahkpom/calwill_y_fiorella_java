package com.calwillyfiorella.util;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.ui.menuUtils.MenuHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    private InputUtils() {}

    public static void closeScanner() {
        scanner.close();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

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

    public static String readString(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
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
    public static RowStatus readRowStatus() {
        MenuHelper.printMenuTitle("OPCIONES DISPONIBLES:");
        // Lo hago hardcode porque esto no cambia
        System.out.format("""
                Opción 1. Activo.
                Opción 2. Inactivo.
                Opción 3. Eliminado.
                """);

        int newStatus = AuxiliarFunction.requireUserOption(List.of(1, 2, 3));

        return switch (newStatus) {
            case 1 -> RowStatus.ACTIVE;
            case 2 -> RowStatus.INACTIVE;
            case 3 -> RowStatus.DELETED;
            default -> throw new IllegalStateException("Unexpected value: " + newStatus);
        };
    }
}
