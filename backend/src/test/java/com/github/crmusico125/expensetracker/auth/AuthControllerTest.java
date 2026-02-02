package com.github.crmusico125.expensetracker.auth;

import com.github.crmusico125.expensetracker.auth.dto.UserLoginRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserLoginResponse;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationRequest;
import com.github.crmusico125.expensetracker.auth.dto.UserRegistrationResponse;
import com.github.crmusico125.expensetracker.auth.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void givenValidRequest_whenRegisterUser_thenReturnUserRegistrationResponse() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest("test@example.com", "plainPassword");
        UserRegistrationResponse responseMock = new UserRegistrationResponse(UUID.randomUUID(), "test@example.com");

        when(authService.registerUser(request)).thenReturn(responseMock);

        // When
        UserRegistrationResponse response = authController.registerUser(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        verify(authService).registerUser(request);
    }

    @Test
    void givenExistingEmail_whenRegisterUser_thenThrowIllegalArgumentException() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest("existing@example.com", "plainPassword");

        when(authService.registerUser(request))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // When / Then
        assertThatThrownBy(() -> authController.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(authService).registerUser(request);
    }

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnAccessToken() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("test@example.com", "plainPassword");
        UserLoginResponse loginResponseMock = new UserLoginResponse("mock-jwt-token");

        when(authService.login(loginRequest)).thenReturn(loginResponseMock);

        // When
        UserLoginResponse response = authController.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.authToken()).isEqualTo("mock-jwt-token");
        verify(authService).login(loginRequest);
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenThrowInvalidCredentialsException() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("wrong@example.com", "wrongPassword");

        when(authService.login(loginRequest))
                .thenThrow(new InvalidCredentialsException());

        // When / Then
        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(authService).login(loginRequest);
    }
}
