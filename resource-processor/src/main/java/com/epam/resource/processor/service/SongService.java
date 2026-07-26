package com.epam.resource.processor.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import com.epam.common.api.resource.ResourceApi;
import com.epam.common.api.song.SongApi;
import com.epam.common.dto.kafka.ResourceUploadEvent;
import com.epam.common.dto.song.SongMetadataDto;
import com.epam.resource.processor.exception.Mp3FileParseException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@RequiredArgsConstructor
@Service
public class SongService {

    @NonNull
    private final Mp3Parser parser;
    @NonNull
    private final ResourceApi resourceApi;
    @NonNull
    private final SongApi songApi;
    @NonNull
    private final RetryTemplate httpRetryTemplate;

    @KafkaListener(topics = "${application.kafka.topic.song.name}", groupId = "${spring.application.name}")
    @SneakyThrows
    public void handleSongUploadEvent(ResourceUploadEvent event) {
        var bytes = httpRetryTemplate.execute(() -> resourceApi.downloadMp3(String.valueOf(event.resourceId())).getBody());
        var metadata = parseSongMetadata(event.resourceId(), bytes);
        httpRetryTemplate.execute(() -> songApi.createSongMetadata(metadata));
    }

    @SneakyThrows
    private SongMetadataDto parseSongMetadata(Long fileId, byte[] bytes) {
        try {
            var handler = new BodyContentHandler(-1);
            var metadata = new Metadata();
            var parseContext = new ParseContext();

            parser.parse(new ByteArrayInputStream(bytes), handler, metadata, parseContext);

            var duration = Optional.ofNullable(metadata.get("xmpDM:duration"))
                .map(Double::parseDouble)
                .map(d -> Duration.ofSeconds((long) d.doubleValue()))
                .map(this::toMmSsString)
                .orElse(null);
            return SongMetadataDto.builder()
                .id(fileId)
                .name(metadata.get("dc:title"))
                .album(metadata.get("xmpDM:album"))
                .artist(metadata.get("xmpDM:artist"))
                .duration(duration)
                .year(metadata.get("xmpDM:releaseDate"))
                .build();
        } catch (NumberFormatException | TikaException | IOException | SAXException ex) {
            throw new Mp3FileParseException(ex.getMessage());
        }
    }

    private String toMmSsString(Duration duration) {
        if (duration == null) {
            return null;
        }
        long minutes = duration.toMinutes();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d", minutes, seconds);
    }
}
