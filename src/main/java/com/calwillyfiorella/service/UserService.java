package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.IsNotAnAdminException;
import com.calwillyfiorella.exception.UserDoesNotExistException;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public void createUser(
            Users requestingUser,
            UserRole userRole,
            String      userPassword,
            String      userName,
            String      usereMail,
            String      userPhone,
            String      companyName
    ) {
        if (userRole == UserRole.SUPER_ADMIN)
            throw new IllegalArgumentException("No se puede crear SUPER_ADMINs con este método!");

        if (userRole == UserRole.ADMIN && (requestingUser == null || !requestingUser.isAdmin()))
            throw new IsNotAnAdminException();

        if (getUser(usereMail) != null)
            throw new IllegalArgumentException("El eMail que intenta ingresar ya corresponde a un usuario registrado!");

        this.userRepository.save(
                new Users(
                        userRole,
                        userPassword,
                        userName,
                        usereMail,
                        userPhone,
                        companyName
                )
        );
    }

    public Users getUser(String usereMail) {
        return this.userRepository.findByeMail(usereMail).orElseThrow(UserDoesNotExistException::new);
    }

    public List<Users> getAllUsers() { return this.userRepository.findAll(); }
}
