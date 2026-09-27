package com.calwillyfiorella.util;

import com.calwillyfiorella.Main;

import java.util.Scanner;

public final class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    private InputUtils() {}

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
