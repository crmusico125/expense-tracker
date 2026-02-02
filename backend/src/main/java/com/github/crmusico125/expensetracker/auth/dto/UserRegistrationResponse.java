package com.github.crmusico125.expensetracker.auth.dto;

import com.github.crmusico125.expensetracker.user.User;

import java.util.UUID;

public record UserRegistrationResponse(
        UUID id,
        String email
) {

    public static UserRegistrationResponse from(User user) {
        return new UserRegistrationResponse(user.getId(), user.getEmail());
    }

}
