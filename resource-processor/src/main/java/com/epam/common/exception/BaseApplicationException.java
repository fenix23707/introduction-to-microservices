package com.epam.common.exception;

import java.util.Map;

import lombok.Getter;

@Getter
public abstract class BaseApplicationException extends RuntimeException {

    private final Map<String, String> details;

    public BaseApplicationException(String message) {
        this(message, Map.of());
    }

    public BaseApplicationException(String message, Map<String, String> details) {
        super(message);
        this.details = details;
    }

    public BaseApplicationException(String message, Map<String, String> details, Throwable cause) {
        super(message, cause);
        this.details = details;
    }
}
