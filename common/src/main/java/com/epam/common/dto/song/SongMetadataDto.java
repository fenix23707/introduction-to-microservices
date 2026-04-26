package com.epam.common.dto.song;

import static com.epam.common.jackson.MmSsDurationDeserializer.SONG_METADATA_DURATION_REGEX;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SongMetadataDto(
    @NotNull(message = "Id cannot be null")
    Long id,

    @Size(min = 1, max = 100, message = "Song name must be between 1 and 100 characters")
    @NotNull(message = "Song name is required")
    String name,

    @Size(min = 1, max = 100, message = "Artist name must be between 1 and 100 characters")
    @NotNull(message = "Artist name is required")
    String artist,

    @Size(min = 1, max = 100, message = "Album name must be between 1 and 100 characters")
    @NotNull(message = "Album name is required")
    String album,

    @Pattern(regexp = SONG_METADATA_DURATION_REGEX, message = "Duration must be in mm:ss format with leading zeros")
    @NotNull(message = "Duration is required")
    String duration,

    @Pattern(regexp = "^(19\\d{2}|20\\d{2})$", message = "Year must be between 1900 and 2099")
    @NotNull(message = "Year is required")
    String year
) {
}
