package com.epam.storage.exception;

import com.epam.storage.dto.StorageType;

import org.springframework.http.HttpStatus;

public class StorageNotFound extends BaseApplicationException{

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;


    private StorageNotFound(String message) {
        super(message, STATUS);
    }

    public static StorageNotFound byType(StorageType type) {
        return new StorageNotFound("Storage with type %s not found".formatted(type));
    }
}
