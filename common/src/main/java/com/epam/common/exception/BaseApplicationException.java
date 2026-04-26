package com.epam.common.exception;

import java.util.Map;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, String> details;

    public BaseApplicationException(String message, HttpStatus status) {
        this(message, status, Map.of());
    }

    public BaseApplicationException(String message, HttpStatus status, Map<String, String> details) {
        super(message);
        this.status = status;
        this.details = details;
    }

    public BaseApplicationException(String message, HttpStatus status, Map<String, String> details, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.details = details;
    }
}
