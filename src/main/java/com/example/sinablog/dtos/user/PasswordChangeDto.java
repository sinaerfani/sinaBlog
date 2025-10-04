package com.example.sinablog.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDto {

    @NotBlank(message = "Current.password.is.required")
    private String currentPassword;

    @NotBlank(message = "New.password.is.required")
    @Size(min = 6, message = "New.password.must.be.at.least.6.characters")
    private String newPassword;

    @NotBlank(message = "Password.confirmation.is.required")
    private String confirmPassword;


    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}