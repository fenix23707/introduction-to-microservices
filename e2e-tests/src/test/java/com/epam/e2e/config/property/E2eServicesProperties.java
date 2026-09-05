package com.epam.e2e.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "e2e")
public record E2eServicesProperties(ResourceService resourceService, SongService songService, Keycloak keycloak) {

    public record ResourceService(String baseUrl) {
    }

    public record SongService(String baseUrl) {
    }

    public record Keycloak(String tokenUri, String clientId, String clientSecret) {
    }
}
