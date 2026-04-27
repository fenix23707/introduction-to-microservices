package com.epam.common.dto.song;

import lombok.Builder;

@Builder
public record SongMetadataDto(
    Long id,

    String name,

    String artist,

    String album,

    String duration,

    String year
) {
}
