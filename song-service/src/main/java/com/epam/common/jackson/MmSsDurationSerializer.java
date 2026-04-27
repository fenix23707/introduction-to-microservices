package com.epam.common.jackson;

import java.time.Duration;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class MmSsDurationSerializer extends ValueSerializer<Duration> {

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        gen.writeString(toMmSsString(value));
    }

    public static String toMmSsString(Duration duration) {
        if (duration == null) {
            return null;
        }
        long minutes = duration.toMinutes();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d", minutes, seconds);
    }
}
