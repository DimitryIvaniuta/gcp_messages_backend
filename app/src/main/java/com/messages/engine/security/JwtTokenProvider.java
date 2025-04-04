package com.messages.engine.security;

import com.messages.engine.exception.KeyLoadingException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.*;
import com.messages.engine.exception.JwtAuthenticationException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.messages.engine.service.SecretManagerService;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SecretManagerService secretManagerService;

    @Value("${gcp.secret.jwt-private-key}")
    private String privateKeySecretName;

    @Value("${gcp.secret.jwt-public-key}")
    private String publicKeySecretName;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationInMs;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        String privateKeyPem = secretManagerService.getSecret(privateKeySecretName);
        String publicKeyPem = secretManagerService.getSecret(publicKeySecretName);
        this.privateKey = loadPrivateKey(privateKeyPem);
        this.publicKey = loadPublicKey(publicKeyPem);
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(now)
                    .expirationTime(expiryDate)
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            JWSSigner signer = new RSASSASigner(privateKey);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Error generating JWT token: {}", e.getMessage(), e);
            throw new JwtAuthenticationException("Could not generate token", e);
        }
    }

    public String getUsernameFromJWT(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (validateSignature(signedJWT)) {
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                return claimsSet.getSubject();
            } else {
                throw new JwtAuthenticationException("Invalid JWT signature");
            }
        } catch (ParseException e) {
            log.error("Error parsing JWT token: {}", e.getMessage(), e);
            throw new JwtAuthenticationException("Could not parse token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!validateSignature(signedJWT)) {
                log.error("JWT token signature validation failed");
                return false;
            }
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationTime = claimsSet.getExpirationTime();
            return new Date().before(expirationTime);
        } catch (ParseException e) {
            log.error("Error parsing JWT token: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean validateSignature(SignedJWT signedJWT) {
        try {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);
            return signedJWT.verify(verifier);
        } catch (JOSEException e) {
            log.error("Error validating JWT signature: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Loads an RSA private key from a PEM-formatted string.
     *
     * @param pem the PEM-formatted private key string.
     * @return the PrivateKey object.
     * @throws KeyLoadingException if the key cannot be loaded.
     */
    private PrivateKey loadPrivateKey(String pem) {
        try {
            String privateKeyPEM = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("Failed to load private key: {}", e.getMessage(), e);
            throw new KeyLoadingException("Failed to load private key", e);
        }
    }


    /**
     * Loads an RSA public key from a PEM-formatted string.
     *
     * @param pem the PEM-formatted public key string.
     * @return the PublicKey object.
     * @throws KeyLoadingException if the key cannot be loaded.
     */
    private PublicKey loadPublicKey(String pem) {
        try {
            String publicKeyPEM = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("Failed to load public key: {}", e.getMessage(), e);
            throw new KeyLoadingException("Failed to load public key", e);
        }
    }

}
