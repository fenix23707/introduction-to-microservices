package com.epam.common.exception;

import org.springframework.http.HttpStatus;

public class CsvIdsLengthExceededException extends BaseApplicationException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public CsvIdsLengthExceededException(String rawIds, long maxLength) {
        super("CSV string is too long: received %s characters, maximum allowed is %s".formatted(rawIds.length(), maxLength), STATUS);
    }
}
