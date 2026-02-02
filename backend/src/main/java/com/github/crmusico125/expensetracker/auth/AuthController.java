package com.github.crmusico125.expensetracker.auth;

import com.github.crmusico125.expensetracker.auth.dto.UserLoginRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationResponse;
import com.github.crmusico125.expensetracker.auth.dto.UserLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public UserRegistrationResponse registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user")
    public UserLoginResponse login(
            @Valid @RequestBody UserLoginRequest request) {
        return authService.login(request);
    }
}
