package com.epam.e2e.client;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class PlatformApiClient {

    private final RestClient resourceServiceClient;
    private final RestClient songServiceClient;

    public PlatformApiClient(@Qualifier("resourceServiceClient") RestClient resourceServiceClient,
                             @Qualifier("songServiceClient") RestClient songServiceClient) {
        this.resourceServiceClient = resourceServiceClient;
        this.songServiceClient = songServiceClient;
    }

    public RawHttpResponse uploadMp3(byte[] fileBytes, String contentType) {
        return exchange(resourceServiceClient.post()
            .uri("/resources")
            .contentType(MediaType.parseMediaType(contentType))
            .body(fileBytes));
    }

    public RawHttpResponse getSongMetadata(long id) {
        return exchange(songServiceClient.get().uri("/songs/{id}", id));
    }

    public boolean isResourceServiceReachable() {
        return isReachable(resourceServiceClient, "/resources/1");
    }

    public boolean isSongServiceReachable() {
        return isReachable(songServiceClient, "/songs/1");
    }

    private boolean isReachable(RestClient client, String path) {
        try {
            client.get().uri(path).exchange((request, response) -> response.getStatusCode(), false);
            return true;
        } catch (ResourceAccessException ex) {
            return false;
        }
    }

    private RawHttpResponse exchange(RestClient.RequestHeadersSpec<?> requestSpec) {
        return requestSpec.exchange(
            (request, response) -> {
                try {
                    var body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                    return new RawHttpResponse(response.getStatusCode().value(), body);
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }, false
        );
    }
}
