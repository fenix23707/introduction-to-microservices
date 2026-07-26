package com.epam.e2e.dto;

import java.util.Map;

public record ExceptionDto(
    String errorCode,
    String errorMessage,
    Map<String, String> details
) {
}
