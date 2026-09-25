package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.IsNotAnAdminException;
import com.calwillyfiorella.exception.UserDoesNotExistException;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.repository.AuthRepository;

public class AuthService {
    private UserService userService;

    public AuthService(UserService userService) { this.userService = userService; }

    public void userLogin(String eMail, String password, boolean isAdmin) {
        AuthRepository.save(this.userService.getAllUsers().stream()
                .filter(u ->
                        u.getUsereMail().equalsIgnoreCase(eMail) &&
                                u.getUserPassword().equals(password) &&
                                (!isAdmin || u.isAdmin())
                )
                .findFirst()
                .orElseThrow(UserDoesNotExistException::new)
        );
    }

    public void logOut() { AuthRepository.save(null); }

    public static Users getActualUser() { return AuthRepository.getActualUser(); }

    public static boolean actualUserIsAdmin() { return AuthRepository.getActualUser().isAdmin(); }

    public static void checkActualUserIsAdmin() {
        if (!actualUserIsAdmin()) throw new IsNotAnAdminException();
    }
}
