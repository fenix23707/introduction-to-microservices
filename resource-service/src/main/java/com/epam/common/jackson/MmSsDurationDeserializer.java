package com.epam.common.jackson;

import java.time.Duration;
import java.util.regex.Pattern;

import com.epam.common.exception.JacksonParseException;

import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class MmSsDurationDeserializer extends ValueDeserializer<Duration> {
    public static final String SONG_METADATA_DURATION_REGEX = "^[0-5]\\d:[0-5]\\d$";
    private static final Pattern DURATION_REGEX = Pattern.compile(SONG_METADATA_DURATION_REGEX);

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctx) {
        var duration = p.getString();
        if (StringUtils.isBlank(duration) || !DURATION_REGEX.matcher(duration).matches()) {
            throw new JacksonParseException("Duration must be in mm:ss format (e.g., 05:30), got: %s".formatted(duration));
        }
        var parts = duration.split(":");
        return Duration.ofMinutes(Long.parseLong(parts[0]))
            .plusSeconds(Long.parseLong(parts[1]));
    }
}