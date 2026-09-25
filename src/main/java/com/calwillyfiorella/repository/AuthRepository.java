package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.Users;

public class AuthRepository {
    private static Users actualUser = null;

    private AuthRepository() {}

    public static void save(Users user) { actualUser = user; }

    public static Users getActualUser() { return actualUser; }
}
