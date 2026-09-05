package com.epam.e2e.client;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.epam.e2e.config.property.E2eServicesProperties;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KeycloakTokenProvider {

    private final RestClient tokenClient;
    private final E2eServicesProperties.Keycloak keycloakProperties;
    private final ReentrantLock lock = new ReentrantLock();

    private volatile String cachedToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    public KeycloakTokenProvider(E2eServicesProperties properties) {
        this.keycloakProperties = properties.keycloak();
        this.tokenClient = RestClient.create();
    }

    public String getAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(expiresAt)) {
            return cachedToken;
        }

        lock.lock();
        try {
            if (cachedToken != null && Instant.now().isBefore(expiresAt)) {
                return cachedToken;
            }
            return fetchNewToken();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private String fetchNewToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", keycloakProperties.clientId());
        form.add("client_secret", keycloakProperties.clientSecret());

        Map<String, Object> response = tokenClient.post()
            .uri(keycloakProperties.tokenUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body(Map.class);

        cachedToken = (String) response.get("access_token");
        int expiresInSeconds = ((Number) response.get("expires_in")).intValue();
        expiresAt = Instant.now().plusSeconds(Math.max(expiresInSeconds - 10, 0));
        return cachedToken;
    }
}
