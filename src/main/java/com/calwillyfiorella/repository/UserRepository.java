package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.Users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final List<Users> users;

    public UserRepository() { users = new ArrayList<>(); }

    public void save(Users user) { users.add(user); }

    public List<Users> findAll() { return Collections.unmodifiableList(users); }

    public Optional<Users> findByeMail(String usereMail) {
        return this.users.stream()
                .filter(u -> u.getUsereMail().equals(usereMail))
                .findFirst();
    }

    public void delete(Users user) { this.users.remove(user); }
}
