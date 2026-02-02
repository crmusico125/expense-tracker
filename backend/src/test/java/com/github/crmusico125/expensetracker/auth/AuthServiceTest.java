package com.github.crmusico125.expensetracker.auth;

import com.github.crmusico125.expensetracker.auth.dto.UserLoginRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserLoginResponse;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationResponse;
import com.github.crmusico125.expensetracker.auth.exception.InvalidCredentialsException;
import com.github.crmusico125.expensetracker.security.jwt.JwtTokenProvider;
import com.github.crmusico125.expensetracker.user.User;
import com.github.crmusico125.expensetracker.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void givenValidRegistrationRequest_whenRegisterUser_thenReturnUserRegistrationResponse() {
        // Given
        String email = "test@example.com";
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword";

        UserRegistrationRequest request = new UserRegistrationRequest(email, rawPassword);

        // Mock password encoding
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Mock user creation
        User user = User.newUser(email, encodedPassword);
        when(userService.createUser(email, encodedPassword)).thenReturn(user);

        // When
        UserRegistrationResponse response = authService.registerUser(request);

        // Then
        assertThat(response).isNotNull()
                            .extracting(UserRegistrationResponse::email)
                            .isEqualTo(email);

        verify(passwordEncoder).encode(rawPassword);
        verify(userService).createUser(email, encodedPassword);
    }

    @Test
    void givenExistingEmail_whenRegisterUser_thenThrowIllegalArgumentException() {
        // Given
        String email = "existing@example.com";
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword";

        UserRegistrationRequest request = new UserRegistrationRequest(email, rawPassword);

        // Mock password encoding
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Mock userService to throw exception when email already exists
        when(userService.createUser(email, encodedPassword))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // When / Then
        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(passwordEncoder).encode(rawPassword);
        verify(userService).createUser(email, encodedPassword);
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnJwtAccessToken() {
        // Given
        String email = "test@example.com";
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword";
        String jwtToken = "jwt-token";

        UserLoginRequest request =
                new UserLoginRequest(email, rawPassword);

        User user = User.newUser(email, encodedPassword);

        when(userService.getByEmailForAuth(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.generateToken(email)).thenReturn(jwtToken);

        // When
        UserLoginResponse response = authService.login(request);

        // Then
        assertThat(response)
                .isNotNull()
                .extracting(UserLoginResponse::authToken)
                .isEqualTo(jwtToken);

        verify(userService).getByEmailForAuth(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(jwtTokenProvider).generateToken(email);
    }

    @Test
    void givenInvalidPassword_whenLogin_thenThrowInvalidCredentialsException() {
        // Given
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";

        UserLoginRequest request =
                new UserLoginRequest(email, rawPassword);

        User user = User.newUser(email, encodedPassword);

        when(userService.getByEmailForAuth(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userService).getByEmailForAuth(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
}

