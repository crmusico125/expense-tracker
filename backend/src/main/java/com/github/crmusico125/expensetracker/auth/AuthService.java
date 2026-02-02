package com.github.crmusico125.expensetracker.auth;

import com.github.crmusico125.expensetracker.auth.dto.UserLoginRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationResponse;
import com.github.crmusico125.expensetracker.auth.exception.InvalidCredentialsException;
import com.github.crmusico125.expensetracker.auth.dto.UserLoginResponse;
import com.github.crmusico125.expensetracker.security.jwt.JwtTokenProvider;
import com.github.crmusico125.expensetracker.user.User;
import com.github.crmusico125.expensetracker.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        var encodedPassword = passwordEncoder.encode(request.password());
        var user = userService.createUser(request.email(), encodedPassword);
        return UserRegistrationResponse.from(user);
    }

    public UserLoginResponse login(UserLoginRequest request) {
        User user = userService.getByEmailForAuth(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return new UserLoginResponse(jwtTokenProvider.generateToken(user.getEmail()));
    }


}
