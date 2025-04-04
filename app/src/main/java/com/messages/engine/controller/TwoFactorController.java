package com.messages.engine.controller;

import com.messages.engine.exception.ResourceNotFoundException;
import com.messages.engine.model.User;
import com.messages.engine.repository.UserRepository;
import com.messages.engine.security.TOTPService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Two-Factor Authentication (2FA) operations.
 * <p>
 * This controller provides endpoints for enabling 2FA by generating TOTP credentials and returning a QR code URL,
 * as well as confirming 2FA by verifying a user-provided TOTP code.
 * </p>
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class TwoFactorController {

    private final UserRepository userRepository;
    private final TOTPService totpService;

    /**
     * Enables Two-Factor Authentication for the currently authenticated user.
     * <p>
     * This endpoint generates new TOTP credentials, updates the user's record with the secret (but leaves 2FA disabled until confirmed),
     * and returns a QR code URL that the user can scan with an authenticator app.
     * </p>
     *
     * @return a ResponseEntity containing the QR code URL.
     */
    @PostMapping("/2fa/enable")
    public ResponseEntity<String> enableTwoFactorAuthentication() {
        try {
            // Retrieve the currently authenticated username
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

            // Generate new TOTP credentials (GoogleAuthenticatorKey contains the secret)
            GoogleAuthenticatorKey credentials = totpService.generateCredentials();
            String secret = credentials.getKey();

            // Update user's record with the new secret; initially mark 2FA as not fully enabled
            user.setTotpSecret(secret);
            user.setTwoFactorEnabled(false);
            userRepository.save(user);

            // Generate a QR code URL (using the otpauth URI format)
            String issuer = "SecureMessagingApp"; // Replace with your actual app name or issuer
            String qrCodeUrl = totpService.generateQrCodeUrl(username, credentials, issuer);

            log.info("2FA enable request for user {} completed; QR code URL generated.", username);
            return ResponseEntity.ok(qrCodeUrl);
        } catch (Exception ex) {
            log.error("Error enabling 2FA: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error enabling 2FA: " + ex.getMessage());
        }
    }

    /**
     * Confirms Two-Factor Authentication for the currently authenticated user.
     * <p>
     * This endpoint verifies the TOTP code provided by the user against the stored secret.
     * If valid, 2FA is marked as enabled.
     * </p>
     *
     * @param totpCode the TOTP code entered by the user.
     * @return a ResponseEntity indicating whether 2FA was successfully enabled.
     */
    @PostMapping("/2fa/confirm")
    public ResponseEntity<String> confirmTwoFactorAuthentication(@RequestParam int totpCode) {
        try {
            // Retrieve the currently authenticated username
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

            // Verify the provided TOTP code using the stored secret
            if (!totpService.verifyCode(user.getTotpSecret(), totpCode)) {
                log.warn("Invalid TOTP code provided for user {}", username);
                return ResponseEntity.badRequest().body("Invalid TOTP code");
            }

            // Mark 2FA as enabled and update the user record
            user.setTwoFactorEnabled(true);
            userRepository.save(user);
            log.info("2FA confirmed and enabled for user {}", username);
            return ResponseEntity.ok("2FA enabled successfully!");
        } catch (Exception ex) {
            log.error("Error confirming 2FA: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error confirming 2FA: " + ex.getMessage());
        }
    }
}
