package com.calwillyfiorella.service;

import com.calwillyfiorella.exception.*;
import com.calwillyfiorella.model.BaseEntity;
import com.calwillyfiorella.model.Users;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.repository.UserRepository;
import com.calwillyfiorella.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public void createUser(Users newUserData) {
        // Valido estas dos primero porque se usan para descartar las siguientes situaciones
        validateUserRole(newUserData.getRole());
        newUserData.setPhone(validateUsereMail(newUserData.geteMail()));

        if (newUserData.getRole() == UserRole.SUPER_ADMIN)
            throw new IllegalArgumentException("No se puede crear SUPER_ADMINs con este método!");

        if (newUserData.getRole() == UserRole.ADMIN && !AuthService.actualUserIsAdmin())
            throw new IsNotAnAdminException();

        if (getUser(newUserData.geteMail()) != null)
            throw new IllegalArgumentException("El eMail que intenta ingresar ya corresponde a un usuario registrado!");

        if (newUserData.getId() == null) {
            newUserData.setId(UUID.randomUUID());
            newUserData.setRowStatus(RowStatus.ACTIVE);
            newUserData.setCreatedAt(LocalDateTime.now());
            newUserData.setUpdatedAt(null);
        } else {
            validateUserId(newUserData.getId());
            BaseEntity.validateRowStatus(newUserData.getRowStatus());
            BaseEntity.validateCreatedAt(newUserData.getCreatedAt());
        }

        validateUserPassword(newUserData.getPassword());
        validateUserName(newUserData.getName());
        newUserData.setPhone(validateUserPhone(newUserData.getPhone()));
//        validateFiledLoginAttempt(newUserData.getFailedLoginsAttempt());

        this.userRepository.save(newUserData);
    }

    public void updateUser(UUID userId, Users newUserData) {
        Users currentUserData = getUser(userId);

        validateUserRole(newUserData.getRole());
        validateUserPassword(newUserData.getPassword());
        validateUserName(newUserData.getName());
        newUserData.setPhone(validateUserPhone(newUserData.getPhone(), userId));
//        validateFiledLoginAttempt(newUserData.getFailedLoginsAttempt());
        BaseEntity.validateRowStatus(newUserData.getRowStatus());

        currentUserData.setRole(newUserData.getRole());
        currentUserData.setPassword(newUserData.getPassword());
        currentUserData.setName(newUserData.getName());
        currentUserData.setPhone(newUserData.getPhone());
//        currentUserData.setFailedLoginsAttempt(newUserData.getFailedLoginsAttempt());
        currentUserData.setRowStatus(newUserData.getRowStatus());
    }

    public Users getUser(String usereMail) {
        return ifUserExits(this.userRepository.findByeMail(usereMail));
    }
    public Users getUser(UUID userId) {
        return ifUserExits(this.userRepository.findById(userId));
    }

    private Users ifUserExits(Optional<Users> userOptional) {
        return userOptional.orElseThrow(UserDoesNotExistException::new);
    }

    public List<Users> getAllUsers() { return this.userRepository.findAll(); }

    public void increaseFailedLoginsAttempt(UUID userId) {
        Users user = getUser(userId);

        user.setFailedLoginsAttempt(user.getFailedLoginsAttempt() + 1);

        if (user.getFailedLoginsAttempt() == 3) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(5));
            System.err.println("Tiene 5 intentos de inicio de sesión fallidos. Se ha bloqueado su inicio de sesión por 5min.");
        }
    }
    public void resetFailedLoginsAttempt(UUID userId) {
        Users user = getUser(userId);
        user.setFailedLoginsAttempt(0);
        user.setLockedUntil(null);
    }

    private void validateUserId(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot not be null!");

        if (getUser(userId) != null)
            throw new UserAlreadyExistException("El ID del Usuario ya existe en la lista!");
    }
    private void validateUserRole(UserRole userRole) {
        Objects.requireNonNull(userRole, "User role cannot be null");
    }
    private void validateUserPassword(String userPassword) {
        ValidationUtils.requireNonBlank(userPassword, "User password cannot be null");
    }
    private void validateUserName(String userName) {
        ValidationUtils.requireNonBlank(userName, "Username cannot be null");
    }
    private String validateUsereMail(String usereMail) {
        return validateUsereMail(usereMail, null);
    }
    private String validateUsereMail(String usereMail, UUID userId) {
        ValidationUtils.requireNonBlank(usereMail, "User email cannot be null");

        String validEmail = ValidationUtils.requireValidEmail(usereMail);

        Optional<Users> existingUser = this.userRepository.findByeMail(usereMail);

        if (existingUser.isPresent() && (userId == null || !existingUser.get().getId().equals(userId)))
            throw new UserAlreadyExistException("Ya existe en la lista un usuario con el aMail " + validEmail + " !");

        return validEmail;
    }
    private String validateUserPhone(String userPhone) {
        return validateUserPhone(userPhone, null);
    }
    private String validateUserPhone(String userPhone, UUID userId) {
        if (userPhone == null) return null;

        String validPhone = ValidationUtils.validateAndGetPhone(userPhone);

        Optional<Users> existingUser = this.userRepository.findByPhone(validPhone);

        if (existingUser.isPresent() && (userId == null || !existingUser.get().getId().equals(userId)))
            throw new UserAlreadyExistException("Ya existe en la lista un usuario con el aMail " + validPhone + " !");

        return validPhone;
    }
//    private void validateFiledLoginAttempt(Integer filedLoginAttempt) {
//        ValidationUtils.requireNonNegative(filedLoginAttempt, "Filed login attempt cannot be negative");
//    }
}
