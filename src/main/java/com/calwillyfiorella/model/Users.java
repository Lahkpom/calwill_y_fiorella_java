package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.Category;
import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.UserRole;
import com.calwillyfiorella.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Users extends BaseEntity {
    private final UUID userId;

    private UserRole        userRole;
    private String          userPassword;
    private String          userName;
    private String          usereMail;
    private String          userPhone;
    private String          companyName;
    private boolean         iseMailVerified;
    private Integer         failedLoginsAttempt;
    private LocalDateTime   lockedUntil;
    private LocalDateTime   lastLoginAt;

    public Users(
            UUID            userId,
            UserRole        userRole,
            String          userPassword,
            String          userName,
            String          usereMail,
            String          userPhone,
            String          companyName,
            boolean         iseMailVerified,
            Integer         failedLoginsAttempt,
            LocalDateTime   lockedUntil,
            LocalDateTime   lastLoginAt,
            RowStatus       rowStatus,
            LocalDateTime   createdAt,
            LocalDateTime   updatedAt
    ) {
        super(rowStatus, createdAt, updatedAt);
        this.userId                 = Objects.requireNonNull(userId, "User id cannot be null");
        this.userRole               = this.validateUserRole(userRole);
        this.userPassword           = this.validateUserPassword(userPassword);
        this.userName               = this.validateUserName(userName);
        this.usereMail              = this.validateUsereMail(usereMail);
        this.userPhone              = this.validateUserPhone(userPhone);
        this.companyName            = companyName;
        this.iseMailVerified        = iseMailVerified;
        this.failedLoginsAttempt    = this.validateFiledLoginAttempt(failedLoginsAttempt);
        this.lockedUntil            = lockedUntil;
        this.lastLoginAt            = lastLoginAt;
    }

    public Users(
            UserRole userRole,
            String userPassword,
            String userName,
            String usereMail,
            String userPhone,
            String companyName
    ) {
        this(
                UUID.randomUUID(),
                userRole,
                userPassword,
                userName,
                usereMail,
                userPhone,
                companyName,
                false,
                0,
                null,
                null,
                RowStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );
    }

    private UserRole validateUserRole(UserRole userRole) {
        return Objects.requireNonNull(userRole, "User role cannot be null");
    }

    private String validateUserPassword(String userPassword) {
        return ValidationUtils.requireNonBlank(userPassword, "User password cannot be null");
    }

    private String validateUserName(String userName) {
        return ValidationUtils.requireNonBlank(userName, "Username cannot be null");
    }

    private String validateUsereMail(String usereMail) {
        // Hay que ver cómo verificar el formato
        return ValidationUtils.requireNonBlank(usereMail, "Usere mail cannot be null");
    }

    private String validateUserPhone(String userPhone) {
        // Modificar esto, phone si puede ser null, solo verificar formato
//        return ValidationUtils.requireNonBlank(userPhone, "User phone cannot be null");
        return userPhone;
    }

    private Integer validateFiledLoginAttempt(Integer filedLoginAttempt) {
        return ValidationUtils.requireNonNegative(filedLoginAttempt, "Filed login attempt cannot be negative");
    }

    public boolean isAdmin() { return this.userRole.equals(UserRole.ADMIN) || this.userRole.equals(UserRole.SUPER_ADMIN); }

    public UUID getUserId() {
        return userId;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public String getUsereMail() {
        return usereMail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public boolean isIseMailVerified() {
        return iseMailVerified;
    }

    public Integer getFailedLoginsAttempt() {
        return failedLoginsAttempt;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
}
