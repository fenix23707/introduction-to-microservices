package com.epam.storage.exception;

import org.springframework.http.HttpStatus;

public class StorageAlreadyExistsException extends BaseApplicationException{

    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public StorageAlreadyExistsException(String bucket, String path) {
        super(String.format("Storage with bucket '%s' and path '%s' already exists", bucket, path), STATUS);
    }
}
