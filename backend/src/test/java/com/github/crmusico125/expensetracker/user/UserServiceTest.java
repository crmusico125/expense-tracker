package com.github.crmusico125.expensetracker.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void givenExistingEmail_whenCreateUser_thenThrowIllegalArgumentException() {
        // Given
        String email = "existing@example.com";
        String password = "hashedpassword";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.createUser(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
}
