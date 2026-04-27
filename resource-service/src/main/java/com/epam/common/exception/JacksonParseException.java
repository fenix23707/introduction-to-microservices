package com.epam.common.exception;

import org.springframework.http.HttpStatus;

public class JacksonParseException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public JacksonParseException(String message) {
        super(message, STATUS);
    }
}
