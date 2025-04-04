package com.messages.engine.service;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.messages.engine.exception.SecretManagerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecretManagerService {

    /**
     * Retrieves a secret from Google Cloud Secret Manager.
     *
     * @param secretName The full resource name of the secret.
     * @return The secret payload as a String.
     */
    public String getSecret(String secretName) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            AccessSecretVersionResponse response = client.accessSecretVersion(secretName);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            log.error("Error accessing secret {}: {}", secretName, e.getMessage(), e);
            throw new SecretManagerException("Failed to load secret: " + secretName, e);

        }
    }
}
