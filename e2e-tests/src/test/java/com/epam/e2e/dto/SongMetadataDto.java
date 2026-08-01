package com.epam.e2e.dto;

public record SongMetadataDto(
    Long id,
    String name,
    String artist,
    String album,
    String duration,
    String year
) {
}
