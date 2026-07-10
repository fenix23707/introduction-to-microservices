package com.epam.resource.processor.exception;

import com.epam.common.exception.BaseApplicationException;

public class Mp3FileParseException extends BaseApplicationException {

    public Mp3FileParseException(String message) {
        super("Mp3 file parse exception: %s".formatted(message));
    }
}
