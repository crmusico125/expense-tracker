package com.github.crmusico125.expensetracker.user;

import com.github.crmusico125.expensetracker.auth.exception.InvalidCredentialsException;
import com.github.crmusico125.expensetracker.user.exception.EmailAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String email, String encodedPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        User user = User.newUser(email, encodedPassword);
        return userRepository.save(user);
    }

    public User getByEmailForAuth(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }
}
