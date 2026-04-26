package com.epam.song.exception;

import com.epam.common.exception.BaseApplicationException;

import org.springframework.http.HttpStatus;

public class SongNotFoundException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    public SongNotFoundException(Long id) {
        super("Song metadata for ID=%s not found".formatted(id), STATUS);
    }
}
