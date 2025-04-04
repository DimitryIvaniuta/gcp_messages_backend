package com.messages.engine.controller;

import com.messages.engine.dto.LoginRequest;
import com.messages.engine.dto.LoginResponse;
import com.messages.engine.exception.ResourceNotFoundException;
import com.messages.engine.model.User;
import com.messages.engine.security.JwtTokenProvider;
import com.messages.engine.security.LoginAttemptService;
import com.messages.engine.service.UserAuthenticateService;
import com.messages.engine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserAuthenticateService userAuthenticateService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String username = request.username();
        String password = request.password();

        // 1. Check if user is blocked
        if (loginAttemptService.isBlocked(username)) {
            throw new BadCredentialsException("Too many failed attempts. Please try again later.");
        }

        try {
            // 2. Authenticate user credentials
            User user = userAuthenticateService.authenticateUser(username, password);

            // 3. If successful, reset attempts
            loginAttemptService.loginSucceeded(username);

            // 4. Generate token
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
            String token = jwtTokenProvider.generateToken(auth);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (ResourceNotFoundException ex) {
            // 5. Record failed login attempt
            loginAttemptService.loginFailed(username);
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
