package com.epam.song.mapper;

import java.time.Duration;

import com.epam.common.dto.song.SongMetadataDto;
import com.epam.common.jackson.MmSsDurationSerializer;
import com.epam.song.entity.SongEntity;

import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;

@Mapper(componentModel = ComponentModel.SPRING)
public interface SongMapper {

    @Mapping(source = "duration", target = "durationMs", qualifiedByName = "durationToMs")
    SongEntity toEntity(SongMetadataDto songDto);

    @Named("durationToMs")
    static int durationToMs(@NonNull String duration) {
        var parts = duration.split(":");
        return (int) Duration.ofMinutes(Long.parseLong(parts[0]))
            .plusSeconds(Long.parseLong(parts[1]))
            .toMillis();
    }

    @Mapping(source = "durationMs", target = "duration", qualifiedByName = "msToDuration")
    SongMetadataDto toDto(SongEntity entity);

    @Named("msToDuration")
    static String msToDuration(int durationMs) {
        var duration = Duration.ofMillis(durationMs);
        return MmSsDurationSerializer.toMmSsString(duration);
    }
}
