package com.calwillyfiorella.ui;

import com.calwillyfiorella.Main;
import com.calwillyfiorella.model.Color;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.service.AuthService;
import com.calwillyfiorella.service.UserService;
import com.calwillyfiorella.ui.utils.*;
import com.calwillyfiorella.util.AuxiliarFunction;
import com.calwillyfiorella.util.InputUtils;

import java.util.ArrayList;
import java.util.List;

public class AuthMenu {
    private final AuthService   authService;
    private final UserService   userService;
    private final MenuHelper    menuHelper;

    public AuthMenu(AuthService authService, UserService userService, MenuHelper menuHelper) {
        this.authService    = authService;
        this.userService    = userService;
        this.menuHelper     = menuHelper;
    }

    public void render(boolean isAdmin, Runnable onCancel) {
        List<MenuOption> options = new ArrayList<>();
        options.add(MenuOption.of("Iniciar Sesión", () -> logIn(isAdmin)));

        if (!isAdmin)
            options.add(MenuOption.of("Crear Cuenta", () -> signUp(false)));

        menuHelper.renderMenuOptions(options, onCancel);
    }

    public void logIn(boolean isAdmin) {
        MenuHelper.printMenuTitle("FORMULARIO INICIO DE SESIÓN");
        System.out.println("### {admin@admin.com / admin} || {cust@cust.com / cust} ###");
        String email    = InputUtils.readString("Ingrese su e-mail: ", true);
        String password = InputUtils.readString("Ingrese su contraseña: ", true);

        try {
            authService.userLogin(email, password, isAdmin);
            Users current = AuthService.getActualUser();
            System.out.format("""
                    -------------------------
                    Sesión iniciada con éxito.
                    Bienvenido %s!
                    -------------------------
                    """,
                    current.getName()
            );
        } catch (Exception e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
        }


    }

    public void signUp(boolean isAdmin) {
        MenuHelper.printMenuTitle("FORMULARIO CREACIÓN DE CUENTA");
        String name     = InputUtils.readString("Ingrese el Nombre: ", true);
        String email    = InputUtils.readString("Ingrese el e-Mail: ", true);
        String password = InputUtils.readString("Ingrese la Contraseña: ", true);

        Users newUser = new Users();
        newUser.setRole(isAdmin ? UserRole.ADMIN : UserRole.CUSTOMER);
        newUser.setName(name);
        newUser.setPassword(password);
        newUser.setEmail(email);

        try {
            userService.createUser(newUser);
            System.out.println("Creación de cuenta exitosa!");
            logIn(isAdmin);
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
        }
    }

    public void renderUserInfo(Runnable onBack) {
        Users currentUser = AuthService.getActualUser();

        MenuHelper.printMenuTitle("INFORMACIÓN DEL USUARIO");

        System.out.format("""
                Compañía: %s.
                ID: %s.
                Role: %s.
                Nombre: %s.
                eMail: %s.
                Teléfono: %s.
                Último inicio de sesión: %s.
                """,
                currentUser.getCompanyName(),
                currentUser.getId(),
                currentUser.getRole(),
                currentUser.getName(),
                currentUser.geteMail(),
                currentUser.getPhone(),
                currentUser.getLastLoginAt()
        );

        List<MenuOption> options = List.of(
                MenuOption.of("Editar Info", () -> updateUser(currentUser, onBack))
        );

        menuHelper.renderMenuOptions(options, onBack);
    }
    private void updateUser(Users currentUserData, Runnable onBack) {
        boolean isUpdate = true;
        boolean isStrict = false;

        try {
            Users newUserData = FormHelper.executeCreateOrUpdateForm("INFORMACIÓN DEL USUARIO", isUpdate, currentUserData, () -> {
                String newName = InputUtils.readString(
                        FormHelper.buildPrompt(
                                "Nombre",
                                currentUserData.getName(),
                                isUpdate
                        ),
                        isStrict
                );
                String newPhone = InputUtils.readString(
                        FormHelper.buildPrompt(
                                "Teléfono",
                                currentUserData.getPhone(),
                                isUpdate
                        ),
                        isStrict
                );
                String newPass = InputUtils.readString(
                        FormHelper.buildPrompt(
                                "Contraseña",
                                currentUserData.getPassword(),
                                isUpdate
                        ),
                        isStrict
                );

                Users user = new Users();
                user.setName(newName);
                user.setPhone(newPhone);
                user.setPassword(newPass);
                return user;
            });

            if (newUserData == null)
                throw new IllegalStateException("El Usuario devuelto por el formulario de actualización de usuarios es un objeto nulo.");

            newUserData.setRole(currentUserData.getRole());
            newUserData.setRowStatus(currentUserData.getRowStatus());

            if (newUserData.getName()       == null) newUserData.setName(currentUserData.getName());
            if (newUserData.getPhone()      == null) newUserData.setPhone(currentUserData.getPhone());
            if (newUserData.getPassword()   == null) newUserData.setPassword(currentUserData.getPassword());

            userService.updateUser(currentUserData.getId(), newUserData);

            System.out.println("El usuario fue actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
        }

        renderUserInfo(onBack);
    }
}