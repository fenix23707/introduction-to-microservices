package com.epam.resource.exception;

import com.epam.common.exception.BaseApplicationException;

import org.springframework.http.HttpStatus;

public class InvalidResourceIdException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidResourceIdException(Long id, String reason) {
        this(String.valueOf(id), reason);
    }

    public InvalidResourceIdException(String id, String reason) {
        super("Invalid value '%s' for ID. %s".formatted(id, reason), STATUS);
    }
}
