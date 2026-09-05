package com.epam.resource.listener;

import java.util.Collections;

import com.epam.resource.dto.kafka.ResourceProcessedEvent;
import com.epam.resource.service.Mp3Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SongProcessedListener {

    private final Mp3Service mp3Service;
    private final JwtDecoder jwtDecoder;

    @KafkaListener(topics = "${application.kafka.topic.song-created.name}", groupId = "${spring.application.name}")
    @SneakyThrows
    public void handleResourceProcessedEvent(ConsumerRecord<String, ResourceProcessedEvent> record) {
        var authHeader = record.headers().lastHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            var raw = new String(authHeader.value());
            var tokenValue = raw.startsWith("Bearer ") ? raw.substring(7) : raw;
            try {
                var jwt = jwtDecoder.decode(tokenValue);
                SecurityContextHolder.getContext().setAuthentication(
                    new JwtAuthenticationToken(jwt, Collections.emptyList())
                );
            } catch (Exception e) {
                log.warn("Kafka record contained an invalid JWT — skipping token relay: {}", e.getMessage());
            }
        }

        try {
            var event = record.value();
            log.info("Received ResourceProcessedEvent for resourceId: {}", event.resourceId());
            mp3Service.moveToPermanentStorage(event.resourceId());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
