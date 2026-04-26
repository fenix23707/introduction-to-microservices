package com.epam.resource.exception;

import com.epam.common.exception.BaseApplicationException;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(Long id) {
        super("Resource with ID=%s not found".formatted(id), STATUS);
    }
}
