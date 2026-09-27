package com.calwillyfiorella.ui;

import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.UserService;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.ui.menuUtils.*;
import com.calwillyfiorella.util.InputUtils;

import java.util.ArrayList;
import java.util.List;

public class AuthMenu {
    private final AuthService authService;
    private final UserService userService;

    public AuthMenu(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public void render(boolean isAdmin, Runnable onCancel) {
        List<MenuOption> options = new ArrayList<>();
        options.add(MenuOption.of("Iniciar Sesión", () -> logIn(isAdmin)));

        if (!isAdmin)
            options.add(MenuOption.of("Crear Cuenta", () -> signUp(false)));

        MenuHelper.renderMenuOptions(options, onCancel);
    }

    public void logIn(boolean isAdmin) {
        MenuHelper.printMenuTitle("FORMULARIO INICIO DE SESIÓN");
        String email = InputUtils.readString("Ingrese su e-mail: ");
        String password = InputUtils.readString("Ingrese su contraseña: ");

        try {
            authService.userLogin(email, password, isAdmin);
            Users current = AuthService.getActualUser();
            System.out.format("""
                    -------------------------
                    Sesión iniciada con éxito.
                    Bienvenido %s!
                    -------------------------
                    """,
                    current.getUserName()
            );
        } catch (Exception e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
        }
    }

    public void signUp(boolean isAdmin) {
        MenuHelper.printMenuTitle("FORMULARIO CREACIÓN DE CUENTA");
        String name     = InputUtils.readString("Ingrese el Nombre: ");
        String email    = InputUtils.readString("Ingrese el e-Mail: ");
        String password = InputUtils.readString("Ingrese la Contraseña: ");

        try {
            userService.createUser(
                    isAdmin ? AuthService.getActualUser() : null,
                    isAdmin ? UserRole.ADMIN : UserRole.CUSTOMER,
                    password,
                    name,
                    email,
                    null,
                    null
            );
            System.out.println("Creación de cuenta exitosa!");
            logIn(isAdmin);
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
        }
    }
}