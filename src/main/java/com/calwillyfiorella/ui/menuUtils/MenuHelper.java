package com.calwillyfiorella.ui.menuUtils;

import java.util.ArrayList;
import java.util.List;

import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.util.InputUtils;

public final class MenuHelper {
    private final AuthService authService;
    private final Runnable onLogout;

    public MenuHelper(AuthService authService, Runnable onLogout) {
        this.authService = authService;
        this.onLogout = onLogout;
    }

    public void renderMenuOptions(List<MenuOption> options, Runnable onBack) {
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
        if (AuthService.getActualUser() != null) fullOptions.add(MenuOption.of("Cerrar Sesión", () -> {
            String userName = AuthService.getActualUser().getUserName();
            authService.logOut();
            System.out.format("""
                    ##########################################################
                    Sesión del usuario %s cerrada.
                    ##########################################################
                    """,
                    userName
            );
            onLogout.run();
        }));

        // Opción 'Finalizar'
        fullOptions.add(MenuOption.of("Finalizar", () -> {
            System.out.println("Gracias por utilizar nuestro sistema!");
            authService.logOut();
            InputUtils.closeScanner();
            System.exit(0);
        }));

        // Imprimir las opciones numeradas
        for (int i = 0; i < fullOptions.size(); i++) {
            System.out.format("    %d. %s.%n", i + 1, fullOptions.get(i).label());
        }
        // Solicitar opción y ejecutar la acción asociada
        int selectedOption = AuxiliarFunction.requireUserOption(fullOptions.size());
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