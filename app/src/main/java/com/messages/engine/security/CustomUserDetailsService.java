package com.messages.engine.security;

import com.messages.engine.model.User;
import com.messages.engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of {@link UserDetailsService} to load user-specific data.
 * <p>
 * This service retrieves a {@link User} from the data store using {@link UserRepository}
 * and builds a Spring Security {@link UserDetails} object to be used for authentication.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    /** Repository for accessing User entities. */
    private final UserRepository userRepository;

    /**
     * Loads the user by username.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated {@link UserDetails} object (never null).
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        // Build and return the UserDetails object using Spring Security's User builder.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities("USER")  // For simplicity, we assign a "USER" role; extend as needed.
                .build();
    }
}
