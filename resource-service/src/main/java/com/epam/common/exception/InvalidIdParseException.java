package com.epam.common.exception;

import java.util.Collections;

import org.springframework.http.HttpStatus;

public class InvalidIdParseException extends BaseApplicationException {

    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Invalid ID format: '%s'. Only positive integers are allowed";
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidIdParseException(String rawValue) {
        super(EXCEPTION_MESSAGE_TEMPLATE.formatted(rawValue), STATUS);
    }

    public InvalidIdParseException(String rawValue, Throwable cause) {
        super(EXCEPTION_MESSAGE_TEMPLATE.formatted(rawValue), STATUS, Collections.emptyMap(), cause);
    }
}

