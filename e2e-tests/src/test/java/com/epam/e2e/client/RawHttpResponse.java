package com.epam.e2e.client;

public record RawHttpResponse(int status, String body) {

    public boolean isSuccessful() {
        return status >= 200 && status < 300;
    }

    public boolean isClientError() {
        return status >= 400 && status < 500;
    }
}
