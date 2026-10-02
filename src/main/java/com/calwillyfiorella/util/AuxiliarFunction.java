package com.calwillyfiorella.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class AuxiliarFunction {
    private AuxiliarFunction() {}

    public static Integer requireUserOption(Integer totalOptions, boolean isStrict) {
        return requireUserOption(totalOptions, "Ingresar opción: ",  isStrict);
    }

    public static Integer requireUserOption(Integer totalOptions, String prompt, boolean isStrict) {
        while (true) {
            Integer option = InputUtils.readInt(prompt,  isStrict);

            if (!isStrict && option == null) return null;

            if (option == null || option < 1 || option > totalOptions) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            return option;
        }
    }

    public static Integer requireUserOption(Collection<Integer> allowedOptions, boolean isStrict) {
        return requireUserOption(allowedOptions, "Ingresar opción: ",  isStrict);
    }

    public static Integer requireUserOption(Collection<Integer> allowedOptions, String prompt, boolean isStrict) {
        Set<Integer> validOptions = new HashSet<>(allowedOptions);

        while (true) {
            Integer option = InputUtils.readInt(prompt, isStrict);

            if (!isStrict && option == null) return null;

            if (option == null || !validOptions.contains(option)) {
                System.err.println("El valor ingresado no corresponde a ninguna de las opciones indicadas!");
                continue;
            }
            return option;
        }
    }
}
