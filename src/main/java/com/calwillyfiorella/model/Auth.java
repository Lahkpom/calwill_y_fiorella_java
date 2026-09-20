package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.UserRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Auth {
    private static final List<Users> users = new ArrayList<>();

    private static Users actualUser = null;

    private Auth() {}

    public static void userLogin(String eMail, String password) {
        userLogin(eMail, password, false);
    }

    public static void userLogin(String eMail, String password, boolean isAdmin) {
        actualUser = users.stream()
                .filter(u ->
                        u.getUsereMail().equalsIgnoreCase(eMail) &&
                        u.getUserPassword().equals(password) &&
                        (!isAdmin || u.isAdmin())
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User does not exist or invalid credentials!"));
    }

    public static void createUser(
            Users requestingUser,
            UserRole userRole,
            String userPassword,
            String userName,
            String usereMail,
            String userPhone,
            String companyName
    ) {
        if (userRole == UserRole.SUPER_ADMIN)
            throw new IllegalArgumentException("No se puede crear SUPER_ADMINs con este método!");

        if (userRole == UserRole.ADMIN && (requestingUser == null || !requestingUser.isAdmin()))
            throw new IllegalArgumentException("Solo los usuarios admin pueden crear otros usuarios admin!");

        if (userExists(usereMail))
            throw new IllegalArgumentException("El eMail que intenta ingresar ya corresponde a un usuario registrado!");

        users.add(new Users(
                userRole,
                userPassword,
                userName,
                usereMail,
                userPhone,
                companyName
        ));
    }

    private static boolean userExists(String usereMail) {
        return users.stream().anyMatch(u -> u.getUsereMail().equals(usereMail));
    }

    public static Users getActualUser() { return actualUser; }
    public static List<Users> getUsers() { return Collections.unmodifiableList(users); }
}
