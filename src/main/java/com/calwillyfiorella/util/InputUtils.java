package com.calwillyfiorella.util;

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
}
