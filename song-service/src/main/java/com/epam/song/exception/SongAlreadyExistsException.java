package com.epam.song.exception;

import com.epam.common.exception.BaseApplicationException;

import org.springframework.http.HttpStatus;

public class SongAlreadyExistsException extends BaseApplicationException {
    private final static HttpStatus STATUS = HttpStatus.CONFLICT;

    public SongAlreadyExistsException(Long id) {
        super("Metadata for resource ID=%s already exists".formatted(id), STATUS);
    }
}
