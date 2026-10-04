package com.calwillyfiorella.repository;

import com.calwillyfiorella.model.Users;

import java.util.*;

public class UserRepository {
    private final List<Users> users;

    public UserRepository() { users = new ArrayList<>(); }

    public void save(Users user) { users.add(user); }

    public List<Users> findAll() { return Collections.unmodifiableList(users); }

    public Optional<Users> findByeMail(String usereMail) {
        return this.users.stream()
                .filter(u -> u.geteMail().equalsIgnoreCase(usereMail))
                .findFirst();
    }
    public Optional<Users> findById(UUID userId) {
        return this.users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }
    public Optional<Users> findByPhone(String userPhone) {
        return this.users.stream()
                .filter(u -> u.getPhone() != null && u.getPhone().equals(userPhone))
                .findFirst();
    }

    public void delete(Users user) { this.users.remove(user); }
}
