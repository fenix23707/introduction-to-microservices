package com.epam.resource.processor.exception;

import com.epam.common.exception.BaseApplicationException;

public class InvalidJwtTokenException extends BaseApplicationException {

    public InvalidJwtTokenException(String message) {
        super("Invalid JWT token: %s".formatted(message));
    }
}
