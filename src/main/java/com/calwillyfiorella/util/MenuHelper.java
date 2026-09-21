package com.calwillyfiorella.util;

import com.calwillyfiorella.Main;

import java.util.ArrayList;
import java.util.List;

public final class MenuHelper {

    private MenuHelper() {}

    public static void renderMenuOptions(List<MenuOption> options, Runnable onBack) {
        System.out.format("""
                ########################################################################
                ########################################################################
                Ingrese el número de la opción deseada:
                """);

        // Construir la lista completa de acciones
        List<MenuOption> fullOptions = new ArrayList<>(options);

        // Opción 'Volver' (si tiene menú padre)
        if (onBack != null) fullOptions.add(MenuOption.of("Volver al menú anterior", onBack));

        // Opción 'Finalizar'
        fullOptions.add(MenuOption.of("Finalizar", () -> {
            System.out.format("Gracias por utilizar nuestro sistema!");
            System.exit(0);
        }));

        // Imprimir las opciones numeradas
        for (int i = 0; i < fullOptions.size(); i++) {
            System.out.format("    %d. %s.%n", i + 1, fullOptions.get(i).label());
        }
        // Solicitar opción y ejecutar la acción asociada
        int selectedOption = AuxiliarFuncs.requireUserOption(fullOptions.size());
        fullOptions.get(selectedOption - 1).action().run();
    }

    public static void printMenuTitle(String title) {
        System.out.format("""
                ########################################################################
                ########################################################################
                %s:
                ------------------------------------------------------------------------
                """,
                title.toUpperCase()
        );
    }
}