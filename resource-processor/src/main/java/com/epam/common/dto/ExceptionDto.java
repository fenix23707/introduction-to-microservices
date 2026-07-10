package com.epam.common.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public record ExceptionDto(
    String errorCode,
    String errorMessage,
    Map<String, String> details
) {
}
