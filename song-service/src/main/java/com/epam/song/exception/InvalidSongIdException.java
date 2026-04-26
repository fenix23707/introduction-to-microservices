package com.epam.song.exception;

import com.epam.common.exception.BaseApplicationException;

import org.springframework.http.HttpStatus;

public class InvalidSongIdException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidSongIdException(Long id) {
        this(String.valueOf(id));
    }

    public InvalidSongIdException(String id) {
        super("Invalid value '%s' for ID. Must be a positive integer".formatted(id), STATUS);
    }
}
