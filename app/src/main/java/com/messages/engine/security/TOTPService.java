package com.messages.engine.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for generating and verifying TOTP codes using Warren Strange’s Google Authenticator library.
 * <p>
 * Provides methods to generate new TOTP credentials (which include the secret),
 * generate a QR code URL using the credentials, and verify user-provided TOTP codes.
 * </p>
 */
@Service
@Slf4j
public class TOTPService {

    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    /**
     * Generates new TOTP credentials.
     *
     * @return a GoogleAuthenticatorKey object containing the TOTP secret.
     */
    public GoogleAuthenticatorKey generateCredentials() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        log.info("Generated TOTP secret: {}", key.getKey());
        return key;
    }

    /**
     * Generates a QR code URL using the provided credentials.
     * <p>
     * The generated URL follows the otpauth URI format, which is compatible with most authenticator apps.
     * </p>
     *
     * @param username    the user's username.
     * @param credentials the GoogleAuthenticatorKey credentials.
     * @param issuer      the name of your application or organization.
     * @return a URL string that encodes the otpauth URI.
     */
    public String generateQrCodeUrl(String username, GoogleAuthenticatorKey credentials, String issuer) {
        String otpAuthUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, username, credentials);
        log.info("Generated QR code URL for user {}: {}", username, otpAuthUrl);
        return otpAuthUrl;
    }

    /**
     * Verifies the provided TOTP code against the stored secret.
     *
     * @param secret the TOTP secret in Base32 format.
     * @param code   the TOTP code entered by the user.
     * @return true if the code is valid; false otherwise.
     */
    public boolean verifyCode(String secret, int code) {
        boolean result = googleAuthenticator.authorize(secret, code);
        log.info("TOTP code verification for secret {} returned: {}", secret, result);
        return result;
    }
}
