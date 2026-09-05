package com.epam.resource.processor.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import com.epam.common.api.resource.ResourceApi;
import com.epam.common.api.song.SongApi;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import com.epam.common.dto.kafka.ResourceUploadEvent;
import com.epam.common.dto.song.SongMetadataDto;
import com.epam.resource.processor.config.properties.KafkaProperties;
import com.epam.resource.processor.exception.InvalidJwtTokenException;
import com.epam.resource.processor.exception.Mp3FileParseException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@RequiredArgsConstructor
@Slf4j
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

    @NonNull
    private final KafkaTemplate<@NonNull String, @NonNull ResourceUploadEvent> kafkaTemplate;
    @NonNull
    private final KafkaProperties kafkaProperties;
    @NonNull
    private final RetryTemplate kafkaRetryTemplate;
    @NonNull
    private final JwtDecoder jwtDecoder;

    @KafkaListener(topics = "${application.kafka.topic.song.name}", groupId = "${spring.application.name}")
    @SneakyThrows
    public void handleSongUploadEvent(ConsumerRecord<String, ResourceUploadEvent> record) {
        authenticate(record);

        try {
            var event = record.value();
            var bytes = httpRetryTemplate.execute(() -> resourceApi.downloadMp3(String.valueOf(event.resourceId())).getBody());
            var metadata = parseSongMetadata(event.resourceId(), bytes);
            httpRetryTemplate.execute(() -> songApi.createSongMetadata(metadata));

            kafkaRetryTemplate.invoke(() -> kafkaTemplate.send(buildRecordWithBearer(kafkaProperties.songCreated().getName(), new ResourceUploadEvent(event.resourceId()))));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void authenticate(ConsumerRecord<String, ResourceUploadEvent> record) {
        var authHeader = record.headers().lastHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            throw new InvalidJwtTokenException("Authorization header is missing");
        }

        var raw = new String(authHeader.value());
        var tokenValue = raw.startsWith("Bearer ") ? raw.substring(7) : raw;
        try {
            var jwt = jwtDecoder.decode(tokenValue);
            var auth = new JwtAuthenticationToken(jwt, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            throw new InvalidJwtTokenException(e.getMessage());
        }
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

    private ProducerRecord<String, ResourceUploadEvent> buildRecordWithBearer(String topic, ResourceUploadEvent event) {
        var record = new ProducerRecord<String, ResourceUploadEvent>(topic, event);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            record.headers().add(
                HttpHeaders.AUTHORIZATION,
                ("Bearer " + jwtAuth.getToken().getTokenValue()).getBytes(StandardCharsets.UTF_8)
            );
        }
        return record;
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
