package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.FailedLoginAttemptException;
import com.calwillyfiorella.exception.IsNotAnAdminException;
import com.calwillyfiorella.exception.UserLockedException;
import com.calwillyfiorella.exception.WrongPasswordException;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.repository.AuthRepository;

import java.time.LocalDateTime;

public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) { this.userService = userService; }

    public void userLogin(String eMail, String password, boolean isAdmin) {
        Users user = this.userService.getUser(eMail);

        try {
            if (user.getLockedUntil() != null) {
                if (user.getLockedUntil().isAfter(LocalDateTime.now())) {
                    throw new UserLockedException("La cuenta se encuentra bloqueada temporalmente.");
                } else {
                    user.setLastLoginAt(LocalDateTime.now());
                }
            }

            verifyCredentials(user, password);

            if (isAdmin && !user.isAdmin())
                throw new IsNotAnAdminException("El usuario ingresado no posee rol de administrador.");
        } catch (WrongPasswordException e) {
            this.userService.increaseFailedLoginsAttempt(user.getId());
            throw new FailedLoginAttemptException("Credenciales inválidas. ", e);
        }

        user.setLastLoginAt(LocalDateTime.now());
        this.userService.resetFailedLoginsAttempt(user.getId());

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

    private void verifyCredentials(Users user, String password) {
        if (!user.getPassword().equals(password))
            throw new WrongPasswordException("La contraseña ingresada no es válida.");
    }
}
