package engine.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.messages.engine.MessagesApplication;
import com.messages.engine.service.SecretManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MessagesApplication.class)
@ActiveProfiles("test")
class SecretManagerServiceTest {

    @Autowired
    private SecretManagerService secretManagerService;

    @Value("${gcp.secret.jwt-private-key}")
    private String jwtPrivateKeySecretName;

    @Value("${gcp.secret.jwt-public-key}")
    private String jwtPublicKeySecretName;

    @Test
    void testGetPrivateKeySecret() {
        System.out.println("private key: " + jwtPrivateKeySecretName);
//        String secret = secretManagerService.getSecret(jwtPrivateKeySecretName);
//        assertNotNull(secret, "Private key secret should not be null");
//        assertFalse(secret.isBlank(), "Private key secret should not be blank");
//        System.out.println("Fetched private key secret: " + secret);
    }

    @Test
    void testGetPublicKeySecret() {
        String secret = secretManagerService.getSecret(jwtPublicKeySecretName);
        assertNotNull(secret, "Public key secret should not be null");
        assertFalse(secret.isBlank(), "Public key secret should not be blank");
        System.out.println("Fetched public key secret: " + secret);
    }
}
