package com.epam.common.controller;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.epam.common.dto.ExceptionDto;
import com.epam.common.exception.BaseApplicationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Map<MediaType, String> MEDIA_TYPE_TO_SIMPLE_NAME = Map.of(
        MediaType.parseMediaType("audio/mpeg"), "MP3",
        MediaType.parseMediaType("audio/mpeg;charset=UTF-8"), "MP3"
    );

    @ExceptionHandler(BaseApplicationException.class)
    public ResponseEntity<ExceptionDto> handleBaseApplicationException(BaseApplicationException ex) {
        return buildExceptionDto(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        var rootCause = ex.getMostSpecificCause();
        if (rootCause instanceof BaseApplicationException baseEx) {
            return buildExceptionDto(baseEx);
        }
        return buildExceptionDto(
            HttpStatus.BAD_REQUEST,
            "Malformed request body: %s".formatted(rootCause.getMessage()),
            Collections.emptyMap()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionDto> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildExceptionDto(
            HttpStatus.BAD_REQUEST,
            mediaTypeNotSupportedCustomMessage(ex),
            Collections.emptyMap()
        );
    }

    private String mediaTypeNotSupportedCustomMessage(HttpMediaTypeNotSupportedException ex) {
        var simpleNames = ex.getSupportedMediaTypes().stream()
            .map(it -> MEDIA_TYPE_TO_SIMPLE_NAME.getOrDefault(it, it.toString()))
            .distinct()
            .collect(Collectors.joining(", "));
        return "Invalid file format: %s. Only %s files are allowed".formatted(
            ex.getContentType(),
            simpleNames
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleUnexpected(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", Collections.emptyMap());
    }

    private ResponseEntity<ExceptionDto> buildExceptionDto(BaseApplicationException ex) {
        return buildExceptionDto(ex.getStatus(), ex.getMessage(), ex.getDetails());
    }

    private ResponseEntity<ExceptionDto> buildExceptionDto(HttpStatus statusCode, String message, Map<String, String> details) {
        var body = new ExceptionDto(
            String.valueOf(statusCode.value()),
            message,
            details
        );

        return ResponseEntity
            .status(statusCode)
            .body(body);
    }
}
