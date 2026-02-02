package com.github.crmusico125.expensetracker.user;

import com.github.crmusico125.expensetracker.auth.exception.InvalidCredentialsException;
import com.github.crmusico125.expensetracker.user.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void givenNewEmail_whenCreateUser_thenUserShouldBeSaved() {
        // Given
        String email = "test@example.com";
        String password = "hashedpassword";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        User savedUser = User.newUser(email, password);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser(email, password);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(User::getEmail, User::getPassword)
                .containsExactly(email, password);

        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenExistingEmail_whenCreateUser_thenThrowEmailAlreadyExistsException() {
        // Given
        String email = "existing@example.com";
        String password = "hashedpassword";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.createUser(email, password))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenExistingEmail_whenGetByEmailForAuth_thenReturnUser() {
        // Given
        String email = "test@example.com";
        String encodedPassword = "encodedPassword";

        User user = User.newUser(email, encodedPassword);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // When
        User result = userService.getByEmailForAuth(email);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);

        verify(userRepository).findByEmail(email);
    }

    @Test
    void givenNonExistingEmail_whenGetByEmailForAuth_thenThrowInvalidCredentialsException() {
        // Given
        String email = "missing@example.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.getByEmailForAuth(email))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository).findByEmail(email);
    }

}
