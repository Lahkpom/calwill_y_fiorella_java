package com.calwillyfiorella.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class AuxiliarFunction {
    private AuxiliarFunction() {}

    public static int requireUserOption(Integer totalOptions) {
        return requireUserOption(totalOptions, "Ingresar opción: ");
    }

    public static int requireUserOption(Integer totalOptions, String prompt) {
        while (true) {
            int option = InputUtils.readInt(prompt);
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
            int option = InputUtils.readInt(prompt);
            if (!validOptions.contains(option)) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            return option;
        }
    }
}
