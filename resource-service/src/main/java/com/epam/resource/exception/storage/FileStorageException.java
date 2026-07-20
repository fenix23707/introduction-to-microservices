package com.epam.resource.exception.storage;

import com.epam.common.exception.BaseApplicationException;

import org.springframework.http.HttpStatus;

public class FileStorageException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.SERVICE_UNAVAILABLE;

    public FileStorageException(String message) {
        super(message, STATUS);
    }
}
