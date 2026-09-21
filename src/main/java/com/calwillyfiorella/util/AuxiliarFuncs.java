package com.calwillyfiorella.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.calwillyfiorella.Main;

public class AuxiliarFuncs {
    private AuxiliarFuncs() {}

    public static int requireUserOption(Integer totalOptions) {
        return requireUserOption(totalOptions, "Ingresar opción: ");
    }

    public static int requireUserOption(Integer totalOptions, String prompt) {
        while (true) {
            int option = readInt(prompt);
            if (option < 1 || option > totalOptions) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            return option;
        }
    }

    public static int requireUserOption(Collection<Integer> allowedOptions) {
        return requireUserOption(allowedOptions, "Ingresar opción: ");
    }

    public static int requireUserOption(Collection<Integer> allowedOptions, String prompt) {
        Set<Integer> validOptions = new HashSet<>(allowedOptions);

        while (true) {
            int option = readInt(prompt);
            if (!validOptions.contains(option)) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            return option;
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = Main.scanner.nextLine().trim();

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
            String input = Main.scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.err.println("No puede ingresar un texto vacío.");
                continue;
            }

            return input;
        }
    }
}
