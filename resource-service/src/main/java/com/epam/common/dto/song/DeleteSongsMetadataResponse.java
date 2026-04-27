package com.epam.common.dto.song;

import java.util.List;

public record DeleteSongsMetadataResponse(
    List<Long> ids
) {
}
