package com.epam.resource.exception;

import com.epam.common.exception.BaseApplicationException;

import org.springframework.http.HttpStatus;

public class Mp3FileParseException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public Mp3FileParseException(String message) {
        super("Mp3 file parse exception: %s".formatted(message), STATUS);
    }
}
