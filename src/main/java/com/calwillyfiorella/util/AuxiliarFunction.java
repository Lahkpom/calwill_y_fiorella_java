package com.calwillyfiorella.util;

import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.ProductVariant;
import com.calwillyfiorella.model.enums.NumericSize;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.TargetGender;
import com.calwillyfiorella.ui.utils.MenuHelper;
import com.calwillyfiorella.ui.utils.ListPrinter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

    public static Color requireColor(List<Color> colors, String prompt, boolean isAdmin, boolean isStrict) {
        List<Integer> allowedColorOptions = toListColors(colors, isAdmin);

        if (allowedColorOptions.isEmpty()) return null;

        Integer colorIdx = requireUserOption(
                allowedColorOptions,
                prompt,
                isStrict
        );

        return (colorIdx == null) ? null : colors.get(colorIdx - 1);
    }

    public static List<Integer> toListColors(List<Color> colors,  boolean isAdmin) {
        return ListPrinter.renderList("LISTADO DE COLORES", colors, isAdmin);
    }

    /**
     * Lista los talles numéricos disponibles, solicita la selección del usuario y retorna la constante de NumericSize elegida.
     *
     * @return NumericSize seleccionado por el usuario.
     */
    public static NumericSize requireNumericSize(String prompt, boolean isStrict) {
        MenuHelper.printMenuTitle("TALLES DISPONIBLES:");

        NumericSize[] sizes = NumericSize.values();

        for (int i = 0; i < sizes.length; i++) {
            // Elimina el prefijo "T_" y reemplaza "_" por "/" en talles dobles (ej: T_35_36 -> 35/36)
            String label = sizes[i].name()
                    .replaceFirst("^T_", "")
                    .replace("_", "/");

            System.out.format("Opción %d. %s%n", i + 1, label);
        }

        Integer selectedOption = requireUserOption(sizes.length, prompt, isStrict);

        if (selectedOption == null) return null;

        return sizes[selectedOption - 1];
    }

    /**
     * Lista los género objetivo disponibles, solicita la selección del usuario y retorna la constante de TargetGender elegida.
     *
     * @return TargetGender seleccionado por el usuario.
     */
    public static TargetGender requireTargetGender(String prompt, boolean isStrict) {
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

    /**
     * Hace que el usuario seleccione por consola la opción de una RowStatus y la devuelve.
     *
     * @return RowStatus seleccionado por el usuario o un IllegalStateException
     */
    public static RowStatus requireRowStatus(String prompt, boolean isStrict) {
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
}
