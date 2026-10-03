package com.calwillyfiorella.ui.menuUtils;

import java.util.ArrayList;
import java.util.List;

import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.util.InputUtils;

public final class MenuHelper {
    private final AuthService   authService;
    private final Runnable      onLogout;

    public MenuHelper(AuthService authService, Runnable onLogout) {
        this.authService    = authService;
        this.onLogout       = onLogout;
    }

    /**
     * Muestra en consola las opciones disponibles de cada menú. Internamente, agrega valores por defecto comunes a todos los menus:
     * 'Volver al menú anterior': Devuelve al usuario al menú indicado en el parámetro 'onBack',
     * 'Cerrar Sesión': Setea el actualUser en null y devuelve al usuario al mainMenu (O a donde se le indique como parámetro constructor al generar la instancia),
     * 'Finalizar': Setea el actualUser en null, cierra el scanner, y realiza un System.exit(0)
     *
     * @param options   Recibe la List<MenuOption> con las opciones específicas
     * @param onBack    Recibe a dónde mandar al usuario en caso de seleccionar 'Volver al menú anterior'
     */
    public void renderMenuOptions(List<MenuOption> options, Runnable onBack) {
        // Nueva lista para incluir las opciones comúnes
        List<MenuOption> fullOptions = new ArrayList<>(options);

        // Opción 'Volver' (Solo si viene el onBack, si no se interpreta que es el MainMenu)
        if (onBack != null) fullOptions.add(MenuOption.of("Volver al menú anterior", onBack));

        // Opción 'Cerrar Sesión' (Solo si detecta un usuario con sesión activa)
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

        // Impresión de las opciones
        System.out.format("""
                ########################################################################
                ########################################################################
                Ingrese el número de la opción deseada:
                """);

        for (int i = 0; i < fullOptions.size(); i++) {
            System.out.format("    %d. %s.%n", i + 1, fullOptions.get(i).label());
        }

        // Solicitar al usuario que ingrese la opción que desea
        int selectedOption = AuxiliarFunction.requireUserOption(fullOptions.size(), true);

        // Ejecutar la acción asociada
        fullOptions.get(selectedOption - 1).action().run();
    }

    /**
     * Genera un formato de título.
     *
     * @param title El nombre que debe tener el título (Se le aplicará .toUpperCas())
     */
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