package com.example.sinablog.dtos.user;

import com.example.sinablog.model.enums.RoleName;
import java.time.LocalDateTime;

public class LoginResponseDto {

    private String message;
    private String username;
    private String fullName;
    private RoleName role;
    private LocalDateTime loginTime;
    private boolean success;

    // Constructors
    public LoginResponseDto() {
        this.loginTime = LocalDateTime.now();
    }

    public LoginResponseDto(String message, String username, String fullName,
                            RoleName role, boolean success) {
        this();
        this.message = message;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.success = success;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public RoleName getRole() {
        return role;
    }

    public void setRole(RoleName role) {
        this.role = role;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}