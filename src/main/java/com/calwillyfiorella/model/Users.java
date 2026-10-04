package com.calwillyfiorella.model;

import com.calwillyfiorella.model.enums.RowStatus;
import com.calwillyfiorella.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class Users extends BaseEntity {
    private UUID            userId;
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

    public Users() {}

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
        this.userId                 = userId;
        this.userRole               = userRole;
        this.userPassword           = userPassword;
        this.userName               = userName;
        this.usereMail              = usereMail;
        this.userPhone              = userPhone;
        this.companyName            = companyName;
        this.iseMailVerified        = iseMailVerified;
        this.failedLoginsAttempt    = failedLoginsAttempt;
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

    public void setId(UUID userId) { this.userId = userId; }
    public void setPhone(String userPhone) {
        this.userPhone = userPhone;
        super.afterUpdate();
    }
    public void setRole(UserRole userRole) {
        this.userRole = userRole;
        super.afterUpdate();
    }
    public void setEmail(String usereMail) {
        this.usereMail = usereMail;
        super.afterUpdate();
    }
    public void setPassword(String userPassword) {
        this.userPassword = userPassword;
        super.afterUpdate();
    }
    public void setName(String userName) {
        this.userName = userName;
        super.afterUpdate();
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        super.afterUpdate();
    }
    public void setIseMailVerified(boolean iseMailVerified) {
        this.iseMailVerified = iseMailVerified;
        super.afterUpdate();
    }
    public void setFailedLoginsAttempt(Integer failedLoginsAttempt) {
        this.failedLoginsAttempt = failedLoginsAttempt;
        super.afterUpdate();
    }
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        super.afterUpdate();
    }
    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
        super.afterUpdate();
    }

    public boolean isAdmin() { return this.userRole.equals(UserRole.ADMIN) || this.userRole.equals(UserRole.SUPER_ADMIN); }

    public UUID getId() {
        return userId;
    }
    public UserRole getRole() {
        return userRole;
    }
    public String getPassword() {
        return userPassword;
    }
    public String getName() {
        return userName;
    }
    public String geteMail() {
        return usereMail;
    }
    public String getPhone() {
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
