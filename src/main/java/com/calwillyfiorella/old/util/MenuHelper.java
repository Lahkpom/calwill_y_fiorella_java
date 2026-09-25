package com.calwillyfiorella.util;

import com.calwillyfiorella.Main;
import com.calwillyfiorella.model.Auth;

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

        // Opción 'Cerrar Sesión' (S detecta un usuario con sesión activa)
        if (Auth.getActualUser() != null) fullOptions.add(MenuOption.of("Cerrar Sesión", () -> {
            String userName = Auth.getActualUser().getUserName();
            Auth.logOut();
            System.out.format("""
                    ##########################################################
                    Sesión del usuario %s cerrada.
                    ##########################################################
                    """,
                    userName
            );
            Main.renderMainMenu();
        }));

        // Opción 'Finalizar'
        fullOptions.add(MenuOption.of("Finalizar", () -> {
            System.out.println("Gracias por utilizar nuestro sistema!");
            Auth.logOut();
            Main.scanner.close();
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