package com.messages.engine.service;

import com.messages.engine.exception.ResourceNotFoundException;
import com.messages.engine.model.User;
import com.messages.engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticateService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user by verifying their username and password.
     *
     * @param username the username.
     * @param rawPassword the raw password to check.
     * @return the authenticated User if valid.
     * @throws ResourceNotFoundException if the user is not found or password doesn't match.
     */
    public User authenticateUser(String username, String rawPassword) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ResourceNotFoundException("User", "credentials", username);
        }
        return user;
    }

}
