package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.IsNotAnAdminException;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.repository.AuthRepository;

public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) { this.userService = userService; }

    public void userLogin(String eMail, String password, boolean isAdmin) {
        Users user = this.userService.getUser(eMail);

        if (!user.getPassword().equals(password))
            throw new IllegalArgumentException("La contraseña ingresada no es válida.");

        if (isAdmin && !user.isAdmin())
            throw new IsNotAnAdminException("El usuario ingresado no posee rol de administrador.");

        AuthRepository.save(user);
    }

    public void logOut() { AuthRepository.save(null); }

    public static Users getActualUser() { return AuthRepository.getActualUser(); }

    public static boolean actualUserIsAdmin() {
        Users user = getActualUser();
        return user != null && user.isAdmin();
    }

    public static void checkActualUserIsAdmin() {
        if (!actualUserIsAdmin()) throw new IsNotAnAdminException();
    }
}
