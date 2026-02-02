package com.github.crmusico125.expensetracker.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(

        @Email(message = "Invalid email", regexp = ".+@.+\\..+")
        String email,

        @NotBlank(message = "Password is required")
        String password
) { }
