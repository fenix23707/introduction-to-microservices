package com.epam.resource.dto;

import java.util.List;

public record Mp3DeleteResponse(
    List<Long> ids
) {
}
